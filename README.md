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

## 🆕 Изменения в форке

Основные изменения относительно оригинального плагина:

```
1. **Поддержка серверов** - отключена поддержка всех серверов кроме Paper для улучшения стабильности
2. **Форматирование текста** - вместо устаревших цветов используется современный MiniMessages format
3. **Экономика** - заменена система экономики с Vault на CoinsEngine
4. **Система понижения цены** - добавлена настройка `understating-price.items` для контроля частоты понижения цены
5. **Исправление багов** - исправлена ошибка с понижением цены при маленькой стоимости предмета
6. **Локализация** - все сообщения вынесены в config.yml для удобной поддержки разных языков
7. **Метрика** - отключена система сбора метрик
8. **Скупка"" - теперь для продажи всех предметов надо нажимать SHIFT+ЛКМ
```

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
```

### Admin Commands
```
> Permission: primeseller.admin
- /seller update - Force update seller items
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
