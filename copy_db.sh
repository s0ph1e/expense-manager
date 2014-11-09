#!/bin/sh
adb shell 'run-as ua.pp.appdev.expense cat /data/data/ua.pp.appdev.expense/databases/expensesDB > /sdcard/database.sqlite'
adb pull /sdcard/database.sqlite
