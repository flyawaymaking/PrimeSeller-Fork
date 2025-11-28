# PrimeSeller-Fork

**Fork maintained by [flyawaymaking](https://github.com/flyawaymaking)**  
*Original plugin by [destroydevs](https://github.com/destroydevs/primeseller)*

[![Apache License 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📖 About

PrimeSeller is an indispensable tool for any Minecraft server aiming to create a dynamic and balanced economy. With its help, players can easily and conveniently sell their items, and administrators can control the game's economy.

This is a fork of the original PrimeSeller plugin, updated and maintained by [flyawaymaking](https://github.com/flyawaymaking).

## ✨ Features

- ✅ MiniMessage formats support
- ✅ Full itemstack support
- ✅ Fully customizable
- ✅ Easy to use
- ✅ Random price generation
- ✅ Random item generation
- ✅ Output of the time before the update
- ✅ Optimized performance
- ✅ Fully configurable
- ✅ Ability to sell the entire inventory
- ✅ Limited and unlimited buyer
- ✅ Intuitive for players
- ✅ Supports modern Minecraft versions

## 🆕 Fork Changes

Main changes compared to the original plugin:

```
1. **Server Support** - Support for all servers except Paper has been disabled to improve stability
2. **Text Formatting** - Legacy color codes replaced with modern MiniMessages format
3. **Economy** - Economy system switched from Vault to CoinsEngine
4. **Price Reduction System** - Added `understating-price.items` setting to control price reduction frequency
5. **Bug Fixes** - Fixed price reduction bug with low-value items. Fixed /psell update command issue
6. **Localization** - All messages moved to config.yml for easy multi-language support. Added Russian translation for items
7. **Metrics** - Metrics collection system disabled
8. **Selling** - Now requires SHIFT+LEFT_CLICK to sell all items
9. **Auto Sell** - Added auto-sell feature (sells items currently available for sale)
```

## Auto Sell Usage
### Quick Start
```
Open menu: /autoseller

Enable auto sell: Click button in slot 50

Add items: LEFT_CLICK on items in your inventory

Done! Items will be sold automatically every 5 seconds
```

### 🎯 Main Actions
✅ Add Item
```
In auto sell menu → LEFT_CLICK on item in your inventory

✅ Item will be added to auto sell list
```
❌ Remove Item
```
In auto sell menu → LEFT_CLICK on item in GUI

❌ Item will be removed from auto sell
```

### Commands
```bash
/autoseller          # Open menu
/autoseller on       # Enable
/autoseller off      # Disable
/autoseller toggle   # Toggle
```

### Limits
1) Basic: 0 slots (requires permissions)
2) With permissions: `primeseller.autosell.10` = 10 slots
3) Unlimited: `primeseller.autosell.bypass`

### Features
1) 🔄 Auto-check every 5 seconds
2) 💰 Money goes directly to balance
3) 📢 Chat notifications about sales (can be disabled)
4) Only sells items that are currently available for sale!

## ⚙️ Plugin Installation

### 📥 Download

1. Go to [Releases](../../releases)
2. Download the latest plugin version

### 🚀 Installation

1. Stop your server (if running)
2. Place the `.jar` file in your `plugins/` folder
3. Start the server

## 📋 Requirements

```
- **Server**: Paper (1.21.8+)
- **Java**: 21
- **Required Dependencies**: CoinsEngine
- **Optional Dependencies**: PlaceholderAPI
```

## 🎮 Usage

### Player Commands
```
> without permissions (configurable)
- /seller - Open the seller interface
- /autoseller - Open the autoseller interface
```

### Admin Commands
```
> Permission: primeseller.admin
- /seller update - Force update seller items
- /seller update [limited|unlimitted]- Force update limited|unlimitted seller items
- /seller addunlimited - Add unlimited buyer
- /seller addlimited - Add limited buyer
- /seller reload - Reload plugin configuration
```

## 📄 License

This project is licensed under the [Apache License 2.0](https://opensource.org/licenses/Apache-2.0).

```
Copyright 2025 destroydevs
Copyright 2025 flyawaymaking

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🔗 Links

- **Maintainer**: [flyawaymaking](https://github.com/flyawaymaking)
- **Original Author**: [destroydevs](https://github.com/destroydevs/primeseller)
- **Issues**: [GitHub Issues](../../issues)
- **Releases**: [GitHub Releases](../../releases)
