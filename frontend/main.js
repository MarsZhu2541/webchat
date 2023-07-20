const App = {
    data() {
        return {
            textInput: "",
            // websocketURL: "ws://localhost:8081/websocket/",
            // baseURL: "http://localhost:8081/webchat/",
            websocketURL: "ws://124.221.128.48:8081/websocket/",
            baseURL: "http://124.221.128.48:8081/webchat/",
            historyMessages: "historyMessages",
            onlineUsers: "onlineUsers",
            user: {id: -1, name: "guest"},
            userName:"guest",
            messageList: [],
            userList: ["zwk", "zsj", "zmy", "ymh", "lgh", "zbl"],
            onlineList: [{
                info: '⬜ zwk'
            }, {
                info: '⬜ zsj'
            }, {
                info: '⬜ zmy'
            }, {
                info: '⬜ ymh'
            }, {
                info: '⬜ lgh'
            }, {
                info: '⬜ zbl'
            },

            ]
        }
    }, mounted() {
        this.initUser()
        this.getHistoryMessages()
    }, methods: {
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

            if (localStorage.getItem("userId") !== null) {
                this.user.id = localStorage.getItem("userId")
                this.userName = this.userList[this.user.id]
            }
        },
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
        },
        open: function () {
            this.showMessage("success", "连接成功ヾ(≧▽≦*)o")
            console.log("socket连接成功")
            this.getOnlineUsers();
        },
        error: function () {
            console.log("连接错误")
            this.showMessage('error', '连接出错了TAT')
        },
        getMessage: function (msg) {
            let message = JSON.parse(msg.data)
            switch (message.type) {
                case "CHAT":
                    this.messageList.push(message)
                    this.handleScrollBottom()
                    break;
                case "LOGIN":
                    this.setUserOnline(message.userId, true)
                    break;
                case "LOGOUT":
                    this.setUserOnline(message.userId, false)
                    break;
                default:
                    console.error("UnKnown message type!")
                    break;
            }
        },
        handleScrollBottom() {
            this.$nextTick(() => {
                let scrollElem = this.$refs.scrollDiv;
                scrollElem.scrollTo({top: scrollElem.scrollHeight, behavior: 'smooth'});
            })
        },
        onClose: function () {
            console.log("socket已经关闭")
            this.showMessage('error', '连接断开了°(°ˊДˋ°) ° ')
        }

        , sendText() {
            this.messageList.push({
                userName: this.userName, text: this.textInput, userId: this.user.id
            })
            this.handleScrollBottom()
            this.socket.send(this.textInput)
            this.textInput = ""
        }, showProfile() {

        },
        showMessage(type, msg) {
            this.$message({
                message: msg, type: type,
            })
        },
        getOnlineUsers() {
            let that = this
            axios.get(this.baseURL + this.onlineUsers)
                .then(res => {
                    that.onlineList.forEach(function (entry, index) {
                        if (res.data.includes(index)) {
                            that.setUserOnline(index, true)
                        } else {
                            that.setUserOnline(index, false)
                        }
                    })
                }).catch(err => {
                console.log('错误' + err)
            })
        },
        setUserOnline(id, isOnline) {
            let status = isOnline ? "🟩 " : "⬜ "
            this.onlineList[id].info = status + this.userList[id];
        }
    }, watch: {

        userName(newValue, oldValue) {
            if(newValue === "guest"){
                this.getOnlineUsers();
                return
            }

            this.user.id = this.userList.indexOf(newValue)
            localStorage.setItem("userId", this.user.id)
            if (this.socket) {
                this.socket.close()
            }
            this.initWebSocket();
        }
    },
}

const app = Vue.createApp(App);
app.use(ElementPlus);
app.mount("#app");
