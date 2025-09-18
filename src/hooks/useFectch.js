import { useCallback, useState } from "react"
import axios from "axios"
/**
 * 自定义钩子可以封装其他的钩子 自定义的钩子就是一个普通的函数
 * 就是它的名字需要用use开头
 */
function useFetch() {
    const [data, setData] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const fetchData = useCallback(async () => {
        try {
            setLoading(true)
            const resp = await axios.get('http://localhost:8080/friendListAndLastMsg',
                {
                    params: { username: localStorage.getItem('username') },
                    withCredentials: true
                }
            );
            setData(resp)
            console.log(resp)
        } catch (error) {
            console.log(error)
            setError(error)
        } finally {
            setLoading(false)
        }
    }, [])



    // 最后直接返回需要暴露给外面的返回值
    return { loading, error, data, fetchData }
}

export default useFetch