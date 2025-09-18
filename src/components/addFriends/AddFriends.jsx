import { useEffect, useState } from "react"
import "./AddFriends.less"
import { Input } from "antd"
const AddFrients = (props)=>{
    
    const [friendKeyWord,setFriendKeyWord] = useState('')
    useEffect(()=>{
        console.log(friendKeyWord)
    },[friendKeyWord])

    useEffect(()=>{
        console.log(props)
    },[props])

    return (
        <>
            <div className="addFrient-container">添加好友弹出框</div>
            <Input  placeholder="Basic usage" value={friendKeyWord} onChange={(e)=>setFriendKeyWord(e.target.value)}/>
        </>
    ) 
}


export default AddFrients
