#!/bin/bash

# Скрипт для автоматического тестирования VFS Emulator
echo "=== VFS Emulator Auto Test ==="

# Переходим в корневую директорию проекта
cd "$(dirname "$0")/.." || exit 1

echo "Текущая директория: $(pwd)"

# Компилируем ВСЕ файлы вместе
echo "Компиляция всех файлов..."
javac -d out src/*.java

if [ $? -ne 0 ]; then
    echo "Ошибка компиляции!"
    echo "Показываю список файлов в src:"
    ls -la src/
    read -p "Нажмите Enter для выхода..."
    exit 1
fi

echo "Компиляция завершена успешно"
echo "Содержимое out:"
ls -la out/

echo "Запуск VFS Emulator..."
echo "========================================"

java -cp out Main << 'EOF'
help
ls
cd /home
cd
ls -l
unknown_command
cd dir1 dir2
exit now
exit
EOF

echo "========================================"
echo "Тестирование завершено"
echo "=== Auto Test Complete ==="

read -p "Нажмите Enter для продолжения..."