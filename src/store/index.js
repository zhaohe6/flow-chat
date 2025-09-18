import { configureStore } from "@reduxjs/toolkit";
import { createSlice } from "@reduxjs/toolkit";

// createSlice 创建一个reducer切片 学生管理切片 成绩管理切片 等最终合并为一个store

// 参数是一个配置对象
const stuSlice = createSlice({
    name:"student", // 切片的名字 名字最好不要和其他的切面重复 用来自动生成action中的type属性
    // 当前切片自己的state初始值
    initialState:{
        name:"孙悟空",
        age:18,
        address:"花果山",
        gender:"male"
    },
    // 指定我们的操作，可以直接添加方法
    reducers:{
        // 形参的第一个参数state是一个代理对象，可以直接修改值
        // 想改state的什么属性就直接改就可以了，不仅可以生效而且不会影响其他的属性
        setName(state, action){
            state.name="猪八戒"
        },
    }   
}); 

// actions存储的是slice自动生成的action创建器 调用函数后自动帮我创建action对象
// action对象的格式是：{type:"name/函数名",payload:函数的参数}
console.log(stuSlice.actions)
export const {setName} = stuSlice.actions


// 创建一个store 它的参数也是一个配置对象 根据配置对象返回一个store对象
const store = configureStore({
    reducer:{
        student:stuSlice.reducer
    } // 如果有多个reducer就传一个对象 每一个reducer都是对象的一个属性

})

export default store;