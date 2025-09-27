## test_phase5.sh

echo "=== VFS Emulator Test Script - Phase 5: Additional Commands ==="
echo ""

cd ..

# Создаем папку out если её нет
mkdir -p out

# Компиляция проекта
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

# Создаем тестовые скрипты для этапа 5
echo "Создаем тестовые скрипты для этапа 5..."

# Основной тестовый скрипт с chmod
cat > testScripts/script_chmod.txt << 'EOF'
help
pwd
ls

chmod 755 file1.txt
chmod 644 file2.txt
chmod 777 script.sh

chmod rwxr-xr-x executable
chmod rw-r--r-- document
chmod rwxrwxrwx public_file

ls

chmod 888 invalid_file.txt
chmod abc file1.txt
chmod 755 non_existent_file
chmod 755
chmod

cd folder1
ls
chmod 600 file2.txt
ls
cd ..
pwd

exit
EOF

# Скрипт для тестирования базовой функциональности
cat > testScripts/script_basic.txt << 'EOF'
# Базовые команды VFS
help
pwd
ls
cd folder1
pwd
ls
tail file2.txt
cd ..
chmod 666 file1.txt
ls
pwd
date
clear
exit
EOF

# Скрипт для тестирования обработки ошибок
cat > testScripts/script_errors.txt << 'EOF'
# Тестирование обработки ошибок
help
chmod
chmod 755
chmod invalid file1.txt
chmod 999 file1.txt
chmod rwx-rwx-rwx file1.txt
cd non_existent_directory
tail directory
exit
EOF

echo "Содержимое скриптов:"
echo "=== script_chmod.txt (основной) ==="
cat testScripts/script_chmod.txt
echo ""
echo "=== script_basic.txt ==="
cat testScripts/script_basic.txt
echo ""
echo "=== script_errors.txt ==="
cat testScripts/script_errors.txt
echo ""

# Создаем тестовую файловую структуру
echo "Создаем тестовую файловую структуру..."
mkdir -p test_basic
mkdir -p test_basic/folder1
mkdir -p test_basic/folder2

echo "Hello from file1" > test_basic/file1.txt
echo "Content of file2" > test_basic/file2.txt
echo "Script content here" > test_basic/script.sh
echo "File in folder1" > test_basic/folder1/file2.txt
echo "Another file" > test_basic/folder1/file3.txt
echo "File in folder2" > test_basic/folder2/file4.txt

echo "Тестовая структура создана:"
find test_basic -type f

echo ""

# Запускаем тесты
echo "========================================"
echo "Test 1: Basic functionality"
echo "========================================"
java -cp out Main -v ./test_basic -script testScripts/script_basic.txt
echo ""

echo "========================================"
echo "Test 2: chmod command testing"
echo "========================================"
java -cp out Main -v ./test_basic -script testScripts/script_chmod.txt
echo ""

echo "========================================"
echo "Test 3: Error handling"
echo "========================================"
java -cp out Main -v ./test_basic -script testScripts/script_errors.txt
echo ""

echo "========================================"
echo "Test 4: Interactive mode (краткий тест)"
echo "========================================"
echo "exit" | java -cp out Main -v ./test_basic
echo ""

echo "=== Все тесты завершены ==="
echo "✅ Этап 5: Дополнительные команды - ВЫПОЛНЕН"
echo ""

read -p "Нажмите Enter для выхода..."