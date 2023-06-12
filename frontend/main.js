const App = {
    data() {
        return {
            textInput: "",
            // websocketURL: "ws://localhost:8081/websocket/",
            // baseURL: "http://localhost:8081/webchat/",
            websocketURL: "ws://124.221.128.48:8081/websocket/",
            baseURL: "http://124.221.128.48:8081/webchat/",
            historyMessages: "historyMessages",
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
        this.getHistoryMessages()
    }
    ,
    methods: {
        getHistoryMessages() {
            axios.get(this.baseURL + this.historyMessages)
                .then(res => {
                    this.messageList = res.data
                    this.handleScrollBottom()
                }).catch(err => {
                console.log('错误' + err)
            })
        },
        initUser() {

        }
        ,
        initWebSocket() {
            if (typeof (WebSocket) === "undefined") {
                alert("您的浏览器不支持socket")
            } else {
                // 实例化socket
                this.socket = new WebSocket(this.websocketURL + this.user.id)
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
            this.showMessage("success", "连接成功ヾ(≧▽≦*)o")
            console.log("socket连接成功")
        }
        ,
        error: function () {
            console.log("连接错误")
            this.showMessage('error', '掉线了TAT，重连中。。。')
            this.initWebSocket()
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
        }
        ,
        onClose: function () {
            console.log("socket已经关闭")
            this.showMessage('error', '连接断开了°(°ˊДˋ°) ° ')
        }

        ,
        async sendText() {
            this.messageList.push({
                userName: this.userName,
                text: this.textInput,
                userId: this.user.id
            })
            this.handleScrollBottom()
            this.socket.send(this.textInput)
            this.textInput = ""
        }
        ,
        showProfile() {

        },
        showMessage(type, msg) {
            this.$message({
                message: msg,
                type: type,
            })
        }
    }
    ,
    watch: {

        userName(newValue, oldValue) {
            this.user.id = this.userList.indexOf(newValue)
            if (this.socket) {
                this.socket.close()
            }
            this.initWebSocket();
        }
    }
}

const app = Vue.createApp(App);
app.use(ElementPlus);
app.mount("#app");
