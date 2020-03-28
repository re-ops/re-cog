# Intro

Re-cog is a base set of remote functions that are executable by [Re-gent](https://github.com/re-ops/re-gent), the functions are divided into two categories:

* Resources which are used in provisioning recipes to setup remote machines (see [Re-cipes](https://github.com/re-ops/re-cipes))
* Facts which are use to get information from remote machines including security audit, performance metrics.

All the functions are serializable which means that we can change them locally refresh the REPL and run the latest version on our remote hosts (no restart required!), this provides the same productive [Reloaded](https://re-ops.github.io/re-docs/usage/#reloaded) workflow as local Clojure functions.

Re-cog function have a number of use cases:

* To be used within provisioning recipes.
* To be used in Re-mote pipelines.
* To be directly invoked on a cluster of machines for ad-hoc execution.


It is a part of the [Re-ops](https://re-ops.github.io/re-ops/) project that offers a live coding environment for configuration management.

[![Build Status](https://travis-ci.org/re-ops/re-cog.png)](https://travis-ci.org/re-ops/re-cog)

# Usage

## Provisioning Recipes

## Re-mote pipelines

## Adhoc invocation

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
