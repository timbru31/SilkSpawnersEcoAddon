# SilkSpawnersEcoAddon [![Build Status](http://ci.dustplanet.de/job/SilkSpawnersEcoAddon/badge/icon)](http://ci.dustplanet.de/job/SilkSpawnersEcoAddon/)

## Info
This CraftBukkit plugin adds an optional economy feature (via Vault) to charge the changing of spawners 
* Uses pure SilkSpawners API
* Fallback and flexible configuration of each mob
* Name and ID support
* No multiple charging of a mob

*About the usage statistics*  
This plugin sends usage statistics! If you wish to disable the usage statistics, look at /plugins/PluginMetrics/config.yml!

## License
This plugin is released under the  
*Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)* license.  
Please see [LICENSE.md](LICENSE.md) for more information.

## Standard config
````yaml
# You can configure every entityID/name (without spaces) or a default!
cantAfford: "&e[SilkSpawnersEco] &4Sorry, but you can't change the mob of this spawner, because you have not enough money!"
afford: "&e[SilkSpawnersEco] &2This action costs &e%money%"
sameMob: "&e[SilkSpawnersEco] &2This action was free, because it's the same mob!"
chargeSameMob: false
chargeXP: false
default: 10.5
pig: 7.25
cow: 0.00
````

##Permission
(Fallback to OPs, if no permissions system is found)

| Permission node | Description |
|:----------:|:----------:|
| silkspawners.free | Bypasses the economy check |

## Support
For support visit the dev.bukkit.org page: http://dev.bukkit.org/bukkit-plugins/silkspawnersecoaddon

## Pull Requests
Feel free to submit any PRs here. :)  
Please follow the Sun Coding Guidelines, thanks!

## Usage statistics
[![MCStats](http://mcstats.org/signature/SilkSpawnersEcoAddon.png)](http://mcstats.org/plugin/SilkSpawnersEcoAddon)

## Donation
[![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif "Donation via PayPal")](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=T9TEV7Q88B9M2)

![BitCoin](https://dl.dropboxusercontent.com/u/26476995/bitcoin_logo.png "Donation via BitCoins")  
Address: 1NnrRgdy7CfiYN63vKHiypSi3MSctCP55C
