const App = {
    data() {
        return {
            textInput: "",
            baseURL: "ws://localhost:8081/",
            websocket: "websocket/",
            user: {id: 1},
            messageList: [],
            userList: ["zwk", "zsj", "zmy", "ymh", "lgh", "zbl"],
            userName: "zwk"
        }
    },
    mounted() {
        this.user.id = 0
        this.initUser()
        this.initWebSocket()
    }
    ,
    methods: {
        initUser() {

        }
        ,
        initWebSocket() {
            if (typeof (WebSocket) === "undefined") {
                alert("您的浏览器不支持socket")
            } else {
                if (this.socket) {
                    this.socket.close()
                }
                // 实例化socket
                this.socket = new WebSocket(this.baseURL + this.websocket + this.user.id)
                // 监听socket连接
                this.socket.onopen = this.open
                // 监听socket错误信息
                this.socket.onerror = this.error
                // 监听socket消息
                this.socket.onmessage = this.getMessage
                this.socket.onclose = this.onClose
            }
        }
        ,
        open: function () {
            console.log("socket连接成功")
        }
        ,
        error: function () {
            console.log("连接错误")
        }
        ,
        getMessage: function (msg) {
            this.messageList.push(JSON.parse(msg.data))
            this.handleScrollBottom()
        }
        ,
        handleScrollBottom() {
            this.$nextTick(() => {
                let scrollElem = this.$refs.scrollDiv;
                scrollElem.scrollTo({top: scrollElem.scrollHeight, behavior: 'smooth'});
            })
        },
        send: function () {
            this.socket.send(this.textInput)
        }
        ,
        onClose: function () {
            console.log("socket已经关闭")
        }

        ,
        sendText() {
            this.messageList.push({
                userName: this.userName,
                text: this.textInput
            })
            this.handleScrollBottom()
            this.socket.send(this.textInput)
            this.textInput = ""
        }
        ,
        showProfile() {

        }
    }
    ,
    watch: {

        userName(newValue, oldValue) {
            this.user.id = this.userList.indexOf(newValue)
            this.initWebSocket()
        }
    }
}
const app = Vue.createApp(App);
app.use(ElementPlus);
app.mount("#app");
