<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [📐 `CF`的设计模式](#-cf%E7%9A%84%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F)
  - [使用`CF`异步执行与主逻辑并发以缩短`RT`](#%E4%BD%BF%E7%94%A8cf%E5%BC%82%E6%AD%A5%E6%89%A7%E8%A1%8C%E4%B8%8E%E4%B8%BB%E9%80%BB%E8%BE%91%E5%B9%B6%E5%8F%91%E4%BB%A5%E7%BC%A9%E7%9F%ADrt)
- [🐻 `CF`的最佳实现与使用陷阱](#-cf%E7%9A%84%E6%9C%80%E4%BD%B3%E5%AE%9E%E7%8E%B0%E4%B8%8E%E4%BD%BF%E7%94%A8%E9%99%B7%E9%98%B1)
  - [`CF`创建子`CF`（两个`CF`使用同一线程池），且阻塞等待子`CF`结果](#cf%E5%88%9B%E5%BB%BA%E5%AD%90cf%E4%B8%A4%E4%B8%AAcf%E4%BD%BF%E7%94%A8%E5%90%8C%E4%B8%80%E7%BA%BF%E7%A8%8B%E6%B1%A0%E4%B8%94%E9%98%BB%E5%A1%9E%E7%AD%89%E5%BE%85%E5%AD%90cf%E7%BB%93%E6%9E%9C)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

# 📐 `CF`的设计模式

**_WIP..._**

## 使用`CF`异步执行与主逻辑并发以缩短`RT`

# 🐻 `CF`的最佳实现与使用陷阱

**_WIP..._**

## `CF`创建子`CF`（两个`CF`使用同一线程池），且阻塞等待子`CF`结果

会形成（池型）死锁。

**_WIP..._**
