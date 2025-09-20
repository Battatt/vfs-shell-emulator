#!/bin/bash

echo "=== VFS Emulator Test Scripts ==="
echo ""

cd ..

# Создаем папку out если её нет
mkdir -p out

# Компиляция проекта из папки src - явно указываем все файлы
echo "Компиляция файлов из src/..."
echo "Содержимое src:"
ls -la src/

echo ""
echo "Компилируем..."
javac -d out src/Main.java src/ConsoleEmulator.java src/CommandManager.java src/ConsoleUI.java src/EmulatorException.java src/filesystem/VFS.java src/filesystem/PathResolver.java src/filesystem/VFSDirectory.java src/filesystem/VFSFile.java src/filesystem/VFSNode.java src/filesystem/exceptions/VFSException.java src/filesystem/exceptions/VFSOperationException.java src/filesystem/exceptions/VFSPathException.java

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Ошибка компиляции!"
    echo "Проверьте синтаксис Java файлов в src/"
    read -p "Нажмите Enter для выхода..."
    exit 1
fi

echo ""
echo "✅ Компиляция завершена успешно"
echo "Содержимое out:"
ls -la out/
echo ""

# Создаем папку testScripts если её нет
mkdir -p testScripts

# Создаем тестовые скрипты если их нет
if [ ! -f "testScripts/script.txt" ]; then
    echo "Создаем script.txt..."
    cat > testScripts/script.txt << 'EOF'
help
ls
cd ..
cd ./folder1
tail file2.txt
hello
clear
hello
clear
date
exit
EOF
fi

if [ ! -f "testScripts/script2.txt" ]; then
    echo "Создаем script2.txt..."
    cat > testScripts/script2.txt << 'EOF'
hello
help
clear
date
ls
cd ./folder1
cd ..
ls
hello
exit
EOF
fi

if [ ! -f "testScripts/scriptError.txt" ]; then
    echo "Создаем scriptError.txt..."
    cat > testScripts/scriptError.txt << 'EOF'
help
ls
unknown_command  # Эта команда вызовет ошибку
cd /home
exit
EOF
fi

echo "Содержимое скриптов:"
echo "=== script.txt ==="
cat testScripts/script.txt
echo ""
echo "=== script2.txt ==="
cat testScripts/script2.txt
echo ""
echo "=== scriptError.txt ==="
cat testScripts/scriptError.txt
echo ""

# Test 1: Basic functionality
echo "========================================"
echo "Test 1: Basic commands"
java -cp out Main -v ./test_basic -script testScripts/script.txt
echo ""

# Test 2: Basic functionality
echo "========================================"
echo "Test 2: Basic commands"
java -cp out Main -v ./test_basic -script testScripts/script2.txt
echo ""

# Test 3: Stop on error
echo "========================================"
echo "Test 3: Stop on error behavior"
java -cp out Main -v ./test_basic -script testScripts/scriptError.txt
echo ""

echo "=== All tests completed ==="
read -p "Нажмите Enter для выхода..."