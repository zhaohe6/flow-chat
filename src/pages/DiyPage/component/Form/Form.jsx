import React, { useState } from "react"
import "./Form.less"
function Form(props) {
    const {updateTodoList} = props

    const [date, setDate] = useState("")
    const [des, setDes] = useState("")
    const [time, setTime] = useState("")
    console.log("Form组件重新渲染")
    const clearForm = () => {
        setDate("")
        setDes("")
        setTime("")
    }
    const formSubmitHandler = (e) => {
        e.preventDefault()
        let obj = {
            date,
            des,
            time
        }
        updateTodoList(obj)
        clearForm()
    }
    const handlerDate = (e) => {
        console.log(e)
        setDate(e.target.value)
    }
    const handlerDes = (e) => {
        console.log(e)
        setDes(e.target.value)
    }
    const handlerTime = (e) => {
        console.log(e)
        setTime(e.target.value)
    }
    return (
        <>
            <form id="form-list" onSubmit={formSubmitHandler}>
                <div>
                    <label htmlFor="date">日期</label>
                    <input id="date" type="date" value={date} onChange={handlerDate} />
                </div>
                <div>
                    <label htmlFor="des">描述</label>
                    <input id="des" type="text" value={des} onChange={handlerDes} />
                </div>
                <div>
                    <label htmlFor="time">时长</label>
                    <input id="time" type="text" value={time} onChange={handlerTime} />
                </div>

                <div>
                    <input type="submit" value={'提交'} />
                </div>
            </form>
        </>
    )
}
export default React.memo(Form)