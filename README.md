# Важно
Пока я не могу заниматься проектом из-за технической невозможности.
Найти обновленный плагин с улучшениями и поддержкой новых версий можете тут: [ФОРК](https://github.com/flyawaymaking/PrimeSeller-Fork)

# About
PrimeSeller is an indispensable tool for any Minecraft server aiming to create a dynamic and balanced economy. With its help, players can easily and conveniently sell their items, and administrators can control the game's economy.

## Features: 
1. [MiniMessages-formats](https://docs.papermc.io/adventure/minimessage/format/) support 
2. Full itemstack support 
3. Fully customizable 
4. Easy to use 
5. Random price generation 
6. Random item generation 
7. Output of the time before the update 
8. Optimized 
9. Fully configurable 
10. Ability to sell the entire inventory 
11. Limited and unlimited buyer 
12. Intuitive for players
13. Auto Seller
14. [Pumpkin](https://github.com/Pumpkin-MC/Pumpkin) support in future.

# Plugin Installation

## Download
1. Go to [Releases](https://github.com/destroydevs/primeseller/releases) or [Spigot](https://www.spigotmc.org/resources/108813/))
2. Download the latest plugin version.

## Installation
1. Stop your server (if running)
2. Place the `.jar` file in your `plugins/` folder
3. Start the server

## Requirements
- Server versions: Paper 1.21.8
- Java: 21+
- Dependencies: 
  1) Required: Vault or CoinsEngine
  2) Optional: PlaceholderAPI

## Usage
Permission `primeseller.user` gives players permissions: `primeseller.seller` and `primeseller.autoseller`
Commands:

> Permission - primeseller.seller
- /seller 

> Permission - primeseller.autoseller
- /autoseller

> Permission - primeseller.admin 
- /seller update [limited|unlimited]
- /seller addunlimited
- /seller addlimited
- /seller reload
