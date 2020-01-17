LEIN_SNAPSHOTS_IN_RELEASE=1 lein with-profile package do uberjar
cat bin/stub.sh target/re-cog-0.2.4-standalone.jar > target/re-cog && chmod +x target/re-cog
