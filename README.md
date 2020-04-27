# SilkSpawnersEcoAddon
[![Build Status](https://ci.dustplanet.de/job/SilkSpawnersEcoAddon/badge/icon)](https://ci.dustplanet.de/job/SilkSpawnersEcoAddon/)
[![Build Status](https://travis-ci.org/timbru31/SilkSpawnersEcoAddon.svg?branch=master)](https://travis-ci.org/timbru31/SilkSpawnersEcoAddon)
[![Build the plugin](https://github.com/timbru31/SilkSpawnersEcoAddon/workflows/Build%20the%20plugin/badge.svg)](https://github.com/timbru31/SilkSpawnersEcoAddon/actions?query=workflow%3A%22Build+the+plugin%22)
[![Known Vulnerabilities](https://snyk.io/test/github/timbru31/silkspawnersecoaddon/badge.svg)](https://snyk.io/test/github/timbru31/silkspawnersecoaddon)

[![BukkitDev](https://img.shields.io/badge/BukkitDev-v2.0.0-orange.svg)](https://dev.bukkit.org/projects/silkspawnersecoaddon)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-v2.0.0-orange.svg)](https://www.spigotmc.org/resources/8089/)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Info
This CraftBukkit/Spigot plugin adds an optional economy feature (via Vault or XP or both) to charge different cases
* Charging for
  * Breaking spawners
  * Changing spawners
  * Placing spawners
* Uses pure SilkSpawners API
* Fallback and flexible configuration of each mob
* No multiple charging of a mob (when changing)
* Second command/action as a confirmation needed (optional)

*Third party features, all of them can be disabled*
* bStats for usage statistics

## Standard config
```yaml
# You can configure every mob name (without spaces) or a default!
chargeSameMob: false
chargeXP: false
chargeBoth: false
chargeMultipleAmounts: false
confirmation:
  enabled: false
  delay: 30
default:
  break:
    money: 10.5
    xp: 100
  change:
    money: 10.5
    xp: 100
  place:
    money: 10.5
    xp: 100
pig:
  break:
    money: 7.25
    xp: 200
  change:
    money: 7.25
    xp: 200
  place:
    money: 7.25
    xp: 200
cow:
  break:
    money: 0.0
    xp: 20
  change:
    money: 0.0
    xp: 20
  place:
    money: 0.0
    xp: 20
```

## Commands
/silkspawnerseco reload - Reloads the config file (default: OP)

## Permissions
(Fallback to OPs, if no permissions system is found)

| Permission node     | Description                         |
|:--------------------|:------------------------------------|
| silkspawners.free   | Bypasses the economy check          |
| silkspawners.reload | Allows access to the reload command |

## Support
For support please open an issue here on [GitHub](https://github.com/timbru31/SilkSpawnersEcoAddon/issues/new).

## Pull Requests
Feel free to submit any PRs here. :)  
Please follow the Sun Coding Guidelines, thanks!

## Usage statistics
_stats images are returning soon!_

## Data usage collection of bStats

#### Disabling bStats
The file `./plugins/bStats/config.yml` contains an option to *opt-out*.

#### The following data is **read and sent** to https://bstats.org and can be seen under https://bstats.org/plugin/bukkit/SilkSpawnersEcoAddon
* Your server's randomly generated UUID
* The amount of players on your server
* The online mode of your server
* The bukkit version of your server
* The java version of your system (e.g. Java 8)
* The name of your OS (e.g. Windows)
* The version of your OS
* The architecture of your OS (e.g. amd64)
* The system cores of your OS (e.g. 8)
* bStats-supported plugins
* Plugin version of bStats-supported plugins

## Donation
[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif "Donation via PayPal")](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=T9TEV7Q88B9M2)

![BitCoin](https://dustplanet.de/wp-content/uploads/2015/01/bitcoin-logo-plain.png "Donation via BitCoins")  
1NnrRgdy7CfiYN63vKHiypSi3MSctCP55C

---
Built by (c) Tim Brust and contributors. Released under the MIT license.
