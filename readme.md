# VFS Emulator - Этап 3: Виртуальная файловая система

Эмулятор виртуальной файловой системы с поддержкой работы с VFS в памяти.

## 📋 Требования этапа 3

Реализована поддержка виртуальной файловой системы:

- ✅ Все операции производятся в памяти
- ✅ VFS основана на реальной директории пользователя
- ✅ Поддержка абсолютных и относительных путей
- ✅ Команды для навигации по файловой системе
- ✅ Тестовые скрипты для различных вариантов VFS


## 🚀 Запуск приложения

### Компиляция
```bash
# Компиляция всех файлов
javac -d out src/Main.java src/ConsoleEmulator.java src/CommandManager.java src/ConsoleUI.java src/EmulatorException.java src/filesystem/VFS.java src/filesystem/PathResolver.java src/filesystem/VFSDirectory.java src/filesystem/VFSFile.java src/filesystem/VFSNode.java src/filesystem/exceptions/VFSException.java src/filesystem/exceptions/VFSOperationException.java src/filesystem/exceptions/VFSPathException.java

# Запуск
java -cp out Main