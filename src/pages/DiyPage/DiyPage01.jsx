import { Button } from "antd"
import useConfig from "antd/es/config-provider/hooks/useConfig";
import { useCallback, useContext, useEffect, useMemo, useReducer, useRef, useState } from "react"
import "./DiyPage01.less"
import Form from "./component/Form/Form";
import TodoList from "./component/TodoList/TodoList";
import useFetch from "../../hooks/useFectch";
import { useDispatch, useSelector } from "react-redux";
import { setName } from "../../store";
import { MyContext } from "../../App";
function DiyPage01() {
    const providerContextProps = useContext(MyContext)
    const student = useSelector(state=>state.student)
    
    const [todosFromChild, setTodosFromChild] = useState(null)
    const [count, setCount] = useState(0)
    console.log("Diypage组件重复渲染")
    const { loading, error, data, fetchData } = useFetch()
    useEffect(() => {
        fetchData()//发送请求 
        console.log(student)
        providerContextProps.des="我修改了"
        console.log(providerContextProps)
    }, [])
    useEffect(() => {
        let timer = setTimeout(() => {
            console.log("我要发送请求了...")
        }, 1000);
        return () => {
            clearTimeout(timer)// 会在下一次执行effect执行调用这个
        }
    }, [count])
    const handlerCount = () => {
        setCount(prev => prev + 1)
    }
    // setCount(0)
    const updateTodoList = useCallback((newEle) => {
        setTodosFromChild(newEle)
    }, [])
    function reducer(state, action) {
        switch (action.type) {
            case "incremented_age": {
                console.log(action.type);
                return {
                    ...state, // 先解构
                    age: state.age + 1    // 后覆盖                
                }
            }
            default: {
                console.log("没有匹配的操作")
            }
        }
    }
    const [stateAge, dispatchAge] = useReducer(reducer, { age: 42, address: "大连理工大学" })
    const dispatch = useDispatch();
    const handlerAge = () => {
        dispatchAge({ type: "incremented_age" })
        dispatch(setName())
    }
    return (
        <>
            <div>
                <div>
                    <label htmlFor="state_age">年龄</label>
                    <span id="state_age">{stateAge.age}---{stateAge.address}</span>
                </div>
                <button onClick={handlerAge}>增加年龄</button>
                <button onClick={handlerCount}>增加数值</button>
            </div>
            <Form updateTodoList={updateTodoList} />
            <TodoList newEle={todosFromChild} />
        </>
    )
}

export default DiyPage01