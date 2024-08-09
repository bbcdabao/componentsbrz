# componentsbrz
Componentsbrz is a collection of useful software. Some sub-projects of Componentsbrz are independent Springboot components, such as:



https://chatgpt.com/share/f3cb24b1-b8cd-4cec-b370-84073f813672


ai集合命令行
https://chatgpt.com/share/48ef28bc-919d-468d-8b19-2fe8b566984e



要实现这个布局，你可以通过以下步骤来设置 manageradd 的 flex 布局，使得 el-image 的宽度保持固定，而 additerm 的宽度动态调整：

设置 manageradd 为 display: flex，并且给 additerm 设置 flex-grow，让其在调整窗口宽度时自动调整。

设置 el-image 的宽度为固定值，并确保它们不会随着窗口调整而改变。

下面是具体的 CSS 样式：

html
复制代码
<style scoped>
.manageradd {
  display: flex;
  align-items: flex-start; /* 确保子元素顶部对齐 */
}

.additerm {
  flex-grow: 1; /* 让该元素占据剩余空间 */
}

.el-image {
  margin-left: 20px;
  width: 130px;
  border-radius: 20px;
  flex-shrink: 0; /* 防止 image 随窗口缩小 */
}
</style>
