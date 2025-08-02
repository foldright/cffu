
$$
同步调用action \Rightarrow action在Caller线程中执行 \land action执行在CF挂载action调用返回之前 \tag{0}
$$

这个命题成立。（通过技术上的逻辑分析可以确定，这里不展开论证）

「action执行在CF挂载action调用返回之前」也可以表述成「action在CF挂载action方法中执行」。

令

- 「同步调用action」为命题 $S$ (Sync)
- 「action在Caller线程中执行」为命题 $C$  
  (action execution is in Caller thread)
- 「action执行在CF挂载action调用返回之前」为命题 $B$  
  (action execution is Before action attach finished)

则命题 $(0)$，可以表述成：

$$
S \Rightarrow C \land B \tag{1}
$$

## 证明命题 $(1)$ 两边是充要条件

要证明两边是充要条件，再补充证明逆命题成立；即：

$$
C \land B \Rightarrow S \tag{2}
$$

上面命题 $(2)$ 的等价逆否命题是：

$$
\neg S \Rightarrow \neg C \lor \neg B \tag{3}
$$

对命题 $(3)$ 进行分情况证明：

case 1: $\neg C$，action不在Caller线程执行执行

$$
\neg C \land \neg S \Rightarrow \neg C \lor \neg B
$$

这个Case 1显然成立；只需证明下面的Case 2。

case 2: $C$，action执行在Caller线程执行

$$
C \land \neg S \Rightarrow \neg C \lor \neg B \tag{4}
$$

化简命题 $(4)$（去掉结论中恒假的或部分 $\neg C$ ）：

$$
\neg S \land C \Rightarrow \neg B \tag{5}
$$

### 下面证明命题 $(5)$（其实只是说明下😂）

异步执行Action 且 Action在Caller线程执行，说明

业务Caller线程完成业务操作并归还到线程池后，只有才能再异步执行Action。  
业务操作 包含 挂载操作、设置好了`finishAttach`标志。

## 注意

> 在代码中，当异步`Action`执行（ $\neg S$ ）时，不会立即设置或说原子设置「调用返回」标志，
代码的实现逻辑可能还要更多检查与正确性证明。

这个不是问题了，说明如下：

- 「挂载action操作」 与 「action执行」 同一线程（都在Caller线程中）
  - 「挂载操作」在「action执行」之前完成（返回）：
    - 单线程的异步（就像`NodeJS`），单线程异步Action执行
  - 「挂载操作」在「action执行」之后完成（返回）：
    - 同步执行 action
  - 因为同一线程（效果上是单线程），无竞争条件；检查`finishAttach`标志无需使用并发同步工具如`AtomicBoolean`
- 「挂载action操作」 与 「action执行」不同线程
  - 多线程异步
  - 只通过「检测Caller线程」就正确判断了；无需检查`finishAttach`标志
