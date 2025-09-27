# VFS Emulator - Этап 5: Дополнительные команды

Эмулятор виртуальной файловой системы с поддержкой UNIX-подобных команд, включая команду `chmod`.

## 📋 Требования этапа 5

Реализована поддержка дополнительных команд:

- ✅ Команда `chmod` - виртуальное изменение прав доступа к файлам
- ✅ Тестовые скрипты для всех команд

## 🚀 Компиляция и запуск

```bash
# Компиляция всех файлов
javac -d out src/Main.java src/ConsoleEmulator.java src/CommandManager.java src/ConsoleUI.java src/EmulatorException.java src/filesystem/VFS.java src/filesystem/PathResolver.java src/filesystem/VFSDirectory.java src/filesystem/VFSFile.java src/filesystem/VFSNode.java src/filesystem/exceptions/VFSException.java src/filesystem/exceptions/VFSOperationException.java src/filesystem/exceptions/VFSPathException.java

# Запуск в интерактивном режиме
java -cp out Main -v ./test_basic
