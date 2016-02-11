# SilkSpawnersEcoAddon
[![Build Status](https://ci.dustplanet.de/job/SilkSpawnersEcoAddon/badge/icon)](https://ci.dustplanet.de/job/SilkSpawnersEcoAddon/)
[![Build Status](https://travis-ci.org/timbru31/SilkSpawnersEcoAddon.svg?branch=master)](https://travis-ci.org/timbru31/SilkSpawnersEcoAddon)
[![Circle CI](https://img.shields.io/circleci/project/timbru31/SilkSpawnersEcoAddon.svg)](https://circleci.com/gh/timbru31/SilkSpawnersEcoAddon)
[![Build status](https://ci.appveyor.com/api/projects/status/8c1a9y2tdl8xwhhn?svg=true)](https://ci.appveyor.com/project/timbru31/silkspawnersecoaddon)

## Info
This CraftBukkit/Spigot plugin adds an optional economy feature (via Vault or XP) to charge the changing of spawners
* Uses pure SilkSpawners API
* Fallback and flexible configuration of each mob
* Name and ID support
* No multiple charging of a mob
* Second command/action as a confirmation needed (optional)

*Third party features, all of them can be disabled*
* Metrics for usage statistics

## License
This plugin is released under the
*Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)* license.
Please see [LICENSE.md](LICENSE.md) for more information.

## Standard config
```yaml
# You can configure every entityID/name (without spaces) or a default!
cantAfford: "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough money!"
afford: "&e[SilkSpawnersEco] &2This action costs &e%money%"
sameMob: "&e[SilkSpawnersEco] &2This action was free, because it's the same mob!"
confirmationPending: "&e[SilkSpawnersEco] Remember that changing the spawner costs &2%money%&e, if you want to continue, do the action again!"
noPermission: "&e[SilkSpawnersEco] &4You do not have the permission to perfom this operation!"
commandUsage: "&e[SilkSpawnersEco] &4Command usage: /silkspawnerseco reload"
reloadSuccess: "&e[SilkSpawnersEco] &2Config file successfully reloaded."
chargeSameMob: false
chargeXP: false
chargeMultipleAmounts: false
confirmation:
  enabled: false
  delay: 30
default: 10.5
pig: 7.25
cow: 0.00
```

## Commands
/silkspawnerseco reload - Reloads the config file (default: OP)

## Permissions
(Fallback to OPs, if no permissions system is found)

| Permission node | Description |
|:----------:|:----------:|
| silkspawners.free | Bypasses the economy check |
| silkspawners.reload | Allows access to the reload command |

## Support
For support visit the dev.bukkit.org page: http://dev.bukkit.org/bukkit-plugins/silkspawnersecoaddon

## Pull Requests
Feel free to submit any PRs here. :)
Please follow the Sun Coding Guidelines, thanks!

## Usage statistics
[![MCStats](http://mcstats.org/signature/SilkSpawnersEcoAddon.png)](http://mcstats.org/plugin/SilkSpawnersEcoAddon)

## Data usage collection of Metrics

#### Disabling Metrics
The file ../plugins/Plugin Metrics/config.yml contains an option to *opt-out*

#### The following data is **read** from the server in some way or another
* File Contents of plugins/Plugin Metrics/config.yml (created if not existent)
* Players currently online (not max player count)
* Server version string (the same version string you see in /version)
* Plugin version of the metrics-supported plugin
* Mineshafter status - it does not properly propagate Metrics requests however it is a very simple check and does not read the filesystem

#### The following data is **sent** to http://mcstats.org and can be seen under http://mcstats.org/plugin/SilkSpawnersEcoAddon
* Metrics revision of the implementing class
* Server's GUID
* Players currently online (not max player count)
* Server version string (the same version string you see in /version)
* Plugin version of the metrics-supported plugin

## Donation
[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif "Donation via PayPal")](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=T9TEV7Q88B9M2)

![BitCoin](https://dl.dropboxusercontent.com/u/26476995/bitcoin_logo.png "Donation via BitCoins")
Address: 1NnrRgdy7CfiYN63vKHiypSi3MSctCP55C
