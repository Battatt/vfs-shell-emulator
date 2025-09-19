#!/bin/bash
echo "=== Basic VFS Test ==="

# Создаем чистую тестовую директорию
rm -rf test_basic
mkdir test_basic



# Запускаем эмулятор из корневой директории проекта
echo "Starting VFS emulator..."
cd "C:\Users\batab\IdeaProjects\mireaWork1CM"
java -cp out/production/mireaWork1CM Main -vfs ./test_basic -script ./test_basic/temp_commands.txt

# Возвращаемся назад и очищаем
cd -
rm -f temp_commands.txt
echo "=== Basic Test Complete ==="
read -p "Press Enter to continue..."