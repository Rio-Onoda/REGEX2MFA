<h1>REWB(後方参照付き正規表現)をMFAにして文字列の受理判定をシミュレーション</h1>
・grammer file->AST<br>                         
・AST->MFA<br>
・MFAによる入力文字列の受理判定

## 使用方法 <br>
# コンパイル　
```shell-session 
$ javac -cp ".:antlr-4.13.2-complete.jar" *.java 
$ javac -cp ".;antlr-4.13.2-complete.jar" *.java 
```
<br>

# 実行　
```shell-session
$ java -cp ".:antlr-4.13.2-complete.jar" Main "正規表現" 入力記号列
$ java -cp ".;antlr-4.13.2-complete.jar" Main "正規表現" 入力記号列
```
<br>

# 例　　
```shell-session
$ java -cp ".:antlr-4.13.2-complete.jar" Main "((?:a|b)*)\g1" abab 
$ java -cp ".;antlr-4.13.2-complete.jar" Main "((?:a|b)*)\g1" abab  
```
