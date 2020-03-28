# Intro

Re-cog is a set of serializable functions that can be executed by [Re-gent](https://github.com/re-ops/re-gent).

The functions are divided into a number categories:

* Resources which are used in provisioning recipes to setup remote machines (see [Re-cipes](https://github.com/re-ops/re-cipes)).
* Facts which are use to get information from remote machines including security audit, performance metrics.
* Common shell scripts that we can execute remotely.

We can change the functions on the fly by refreshing the REPL and run the latest version on our remote hosts (no restart required!) using the [Reloaded](https://re-ops.github.io/re-docs/usage/#reloaded) workflow.

Re-cog resources facts and scripts have a number of use cases:

* To be used within provisioning [recipes](https://github.com/re-ops/re-cipes).
* To be used in Re-mote [pipelines](https://re-ops.github.io/re-docs/#abstractions).
* To be directly invoked on a cluster of machines for ad-hoc execution.

It is a part of the [Re-ops](https://re-ops.github.io/re-ops/) project that offers a live coding environment for managing and information gathering from remote systems.

[![Build Status](https://travis-ci.org/re-ops/re-cog.png)](https://travis-ci.org/re-ops/re-cog)

# Usage

### Provisioning Recipes

A recipe is a Clojure namespace which includes a list of functions used to provision a component in a system, each one of those functions use resources/facts and is serializable by using [def-inline](https://github.com/re-ops/re-cog/blob/master/src/re_cog/common/defs.clj#L73).

In the following example we setup a ZSH shell, we start by requiring our Re-cog resources/facts and adding def-inline by using the require-recipe macro:

```clojure
(ns re-cipes.shell
  "Setting up shell"
  (:require
   [re-cog.resources.git :refer (clone)]
   [re-cog.resources.exec :refer [run]]
   [re-cog.common.recipe :refer (require-recipe)]
   [re-cog.facts.config :refer (configuration)]
   [re-cog.resources.download :refer (download)]
   [re-cog.resources.file :refer (symlink directory chmod)]))

(require-recipe)
```
The recipe functions use [resources](https://github.com/re-ops/re-cog/tree/master/src/re_cog/resources) (clone,chown etc..) and [facts](https://github.com/re-ops/re-cog/tree/master/src/re_cog/facts) (configuration) to provision the machine, each of the functions is responsible to a single component of our recipe:

```clojure
(def-inline zsh
  "zsh setup"
  []
  (letfn [(chsh [user]
            (fn []
              (script ("sudo" "/usr/bin/chsh" "-s" "/usr/bin/zsh" ~user))))]
    (let [{:keys [home user]} (configuration)
          dest (<< "~{home}/.tmux")]
      (package "zsh" :present)
      (when-not  (clojure.string/includes? (<< "~{user}:/bin/zsh") (slurp "/etc/passwd"))
        (run (chsh user))))))

(def-inline minimal-zsh
  "Minmal zsh setup"
  []
  (let [{:keys [home user]} (configuration)
        dest (<< "~{home}/.minimal-zsh")]
    (clone "git://github.com/narkisr/minimal-zsh.git" dest)
    (chown dest user user {})
    (symlink (<< "~{home}/.zshrc") (<< "~{dest}/.zshrc"))))
```

### Re-mote pipelines

Re-mote pipeline are using Re-cog scripts/facts/resources:

```clojure
(defn ^{:category :stats} cpu-persist
  "Collect CPU and idle usage with persistence (metrics collection):
     (cpu-persist hs)
  "
  [hs]
  (run> (cpu hs) | (enrich "cpu") | (persist) | (riemann)))
```

The pipeline uses the [cpu](https://github.com/re-ops/re-core/blob/master/src/re_mote/zero/stats.clj#L116) function that extends hosts, here we use both the shell resource function and cpu-script:

```clojure
(extend-type Hosts
  Stats
  ...
  (cpu
    ([this]
     (into-dec (zip this (run-hosts this shell (shell-args cpu-script) timeout) :stats :cpu :usr :sys :idle)))
    ([this _]
     (cpu this)))
  ...
)

```
### Adhoc invocation

In some cases it is useful to invoke Re-cog resrouces/facts/scripts in an adhoc manner (for interactive data collection or during development):

```clojure
; collecting the cpu vulnerabilities from our hosts
(run-hosts (hosts ip :hostname) re-cog.facts.security/cpu-vulns [] [10 :second])
```

# Copyright and license

Copyright [2020] [Ronen Narkis]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
