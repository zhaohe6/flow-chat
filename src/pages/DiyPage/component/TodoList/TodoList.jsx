import React, { useEffect, useReducer, useState } from "react";
import styled from "./TodoList.module.less"
function TodoList(props) {
    const { newEle } = props;
    const todoListInit = [
        {
            date: "2月20日",
            des: "学习九阳神功",
            time: "30分钟"
        },
        {
            date: "3月10日",
            des: "学习降龙十八掌",
            time: "20分钟"
        },
        {
            date: "3月11日",
            des: "学习JavaScript",
            time: "40分钟"
        },
        {
            date: "3月15日",
            des: "学习React",
            time: "80分钟"
        }
    ]
    const [todoList, setTodoList] = useState(todoListInit)
    console.log("todolist组件重新渲染")
    useEffect(() => {
        if (newEle == null) return;
        setTodoList(prev => { // 用了一个prev这个挺好的 这样就不用写todoList 也就不用依赖todoList
            if (Array.isArray(newEle)) {
                return [...newEle, ...prev];
            }
            if (typeof newEle === 'object') {
                return [newEle, ...prev];
            }
            return prev;
        });
    }, [newEle]);
    const deletEele = (des) => {
        const isDelete = window.confirm("此操作不可恢复,是否确认删除？")
        if (isDelete) {
            const filtedList = todoList.filter(ele => {
                if (ele.des === des) {
                    return false
                }
                return true;
            })
            setTodoList(filtedList)
        }


    }

    return (
        <>
            <div className={styled.todoList}>
                <ul>
                    {(Array.isArray(todoList) ? todoList : []).map((item, idx) => {
                        const key = item.id ?? `${item.date}-${idx}`;
                        return (
                            <li key={key} className={styled.liStyle} >
                                <div>{item.date}</div>
                                <div>{item.des}</div>
                                <div>{item.time}</div>
                                <button onClick={() => deletEele(item.des)}>删除</button>
                            </li>
                        )
                    })}
                </ul>
            </div>
        </>
    )
}
// React.memo是一个高阶组件，它的参数是一个组件，返回值是一个包装过的新组件
export default React.memo(TodoList);