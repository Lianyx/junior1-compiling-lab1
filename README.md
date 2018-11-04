# junior1-compiling-lab1

by 161250065 连远翔



## Motivation/Aim

完成编译原理实验作业

## Content description

* 实验报告（本文）

* 代码

* 样例输入输出：
  * lex输入（test.l，resource下）
  * lex compiler输出（lyx.ee.c，根目录下）
  * 字符串输入（test.txt，根目录下）
  * token序列输出（testout.txt，根目录下）

## Ideas/Methods

1. 從xx.l中读入RE和action
2. 每个RE预处理
3. RE => NFA
4. n * NFA => NFA
5. NFA => DFA
6. DFA => lyx.ee.c

## Assumptions

只部分实現了lex的正則規范

可用RE definition，即{xxx}引用

可用(, ), *, |的基本符号

可对上述基本符号进行形如‘\\*’的转义

支持\t\n

<br>

**不支持**error reporting，一旦遇到无法识別的符号，lyx.ee.c的生成文件便会提示错误并中止运行

<br>

**沒有进行**同类项的合并

## Related FA descriptions



## Description of important Data Structures

```java
// 若以string代表RE，处理形如\*的转义字符時较困难，故使用List<RENode>
public class RENode {
    public char ch;
    public Type type;

    public enum Type {
        CH, OP
    }
}
```

```java
// List中的每一個Map代表转换表中的一行，这一行在這個list中的index即为這个状态的编号，char代表边，set(integer)代表通过这一边换到达的下一状态的行号
// 由于在一开始构建小NFA時，只有一个end state且必为最後一個状态，所以不需保存。合成的大NFA有多個end state，但由于整个程序只有一個大NFA，由main函数管理
public class NFA {
    List<Map<Character, Set<Integer>>> table;
}
```

```java
// 兩个list的行号相对應，行号代表DFA状态的编号
public class DFA {
    List<Map<Character, Integer>> table;
    List<Set<Integer>> NFA_states;
}
```

## Description of core Algorithms

1. RE预处理用到了转后序的算法
2. RE => NFA，Thompson算法
3. NFA => DFA，subset construction算法

## Use cases on running

lyx.ee.c过长不方便在此引用，lyx.ee.c的输入和输出分別如下

```
public class Point() {
    public static void main(String[] args) {
        int i = 3;
        if (i != 1fdsa) {
           i *==* j;
        }
    }
} ßßß
```

```
saved word: public
saved word: class
identifier: Point
delimiter: (
delimiter: )
delimiter: {
saved word: public
saved word: static
saved word: void
identifier: main
delimiter: (
saved word: String
delimiter: [
delimiter: ]
identifier: args
delimiter: )
delimiter: {
saved word: int
identifier: i
operater: =
number: 3
delimiter: ;
saved word: if
delimiter: (
identifier: i
operater: !=
number: 1
identifier: fdsa
delimiter: )
delimiter: {
identifier: i
operater: *=
operater: =
operater: *
identifier: j
delimiter: ;
delimiter: }
delimiter: }
delimiter: }
Warning: an error occurs!!!
```

## Problems occurred and related solutions

正則表达式处理转义字符出現困难；解决方法：通过上述List\<RENode\>数据结构代替string表示RE（参考https://github.com/DeepAQ/CompilerLabs）

## Your feelings and comments

掌握了书中的算法，将其落实到代码中，这在不关注性能的情况下并不是特別困难。但是一些细节问题上却很容易出現疏漏。例如在lyx.ee.c的拼接完成後，一个有问题的测例是(a|b)*，这将使c代码无限循环，因为這個正則本身是可以接受ε空字符串的。