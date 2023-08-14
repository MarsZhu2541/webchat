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
            chatSSE: "chat/sse",
            login: "login",
            logoutPath: "logout",
            user: {id: -1, name: "guest"},
            messageList: [],
            userList: ["zwk", "zsj", "zmy", "ymh", "lgh", "zbl", "ChatGPT"],
            chatGPTMessageIndex: 0,
            waitingIntervalId: 0,
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
    },
    async mounted() {
        await this.initUser()
        this.getHistoryMessages()
        setTimeout(this.getOnlineUsers, 800)
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
                //have logged
                this.user.id = localStorage.getItem("userId")
                this.user.name = this.userList[this.user.id]
                this.initWebSocket()
            } else {
                //haven't logged
                const token = this.getQueryString("token")
                if (token === "") {
                    //first log
                    this.showMessage("info", "Will redirect to login page, please wait...（づ￣3￣）づ");
                    window.location.href = this.baseURL + this.login
                } else {
                    try {
                        //redirect from keycloak, validate token
                        const [header, payload, signature] = token.split('.');
                        const decodedPayload = JSON.parse(atob(payload));
                        this.user.name = decodedPayload.preferred_username;
                        this.user.id = this.userList.indexOf(this.user.name)
                        localStorage.setItem("userId", this.user.id)
                        this.initWebSocket()
                    } catch (e) {
                        //invalidate token, redirect to login page
                        this.showMessage("error", "Invalidate token, please login again... (┯。┯∏)");
                        window.location.href = this.baseURL + this.login
                    }
                }
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
        open() {
            this.showMessage("success", "连接成功ヾ(≧▽≦*)o")
            console.log("socket连接成功")
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
                    if (message.text.startsWith("@ChatGPT ")) {
                        this.initChatGPTMessage()
                    }
                    this.handleScrollBottom()
                    break;
                case "LOGIN":
                    this.setUserOnline(message.userId, true)
                    break;
                case "LOGOUT":
                    this.setUserOnline(message.userId, false)
                    break;
                case "CHATGPT_SSE":
                    this.inputChatGPTMessage(message.text)
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
            if(this.textInput===""){
                return;
            }
            this.messageList.push({
                userName: this.user.name, text: this.textInput, userId: this.user.id
            })
            this.handleScrollBottom()
            this.socket.send(this.textInput)
            if (this.textInput.startsWith("@ChatGPT ")) {
                this.initChatGPTMessage()
            }
            this.textInput = ""
        }, showProfile() {
            this.showMessage("success", "Hi, " + this.user.name + "! ๑乛◡乛๑")
        }, logout() {
            this.showMessage("success", "Ok, but you'll come back right? (▼へ▼メ)");
            localStorage.clear()
            this.socket.close()
            window.location.href = this.baseURL + this.logoutPath;
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
        },
        getQueryString(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg); //获取url中"?"符后的字符串并正则匹配
            var context = "";
            if (r != null)
                context = decodeURIComponent(r[2]);
            reg = null;
            r = null;
            return context == null || context == "" || context == "undefined" ? "" : context;
        },
        initChatGPTMessage() {
            let chatGPTMSG = {
                "userId": 6,
                "userName": "ChatGPT",
                "text": ".",
                "type": "CHAT"
            }
            this.messageList.push(chatGPTMSG)
            this.handleScrollBottom()
            this.chatGPTMessageIndex = this.messageList.length - 1;
            let counter = 0
            this.waitingIntervalId = setInterval(() => {
                // 要执行的操作
                if(counter%4===0){
                    this.messageList[this.chatGPTMessageIndex].text = "."
                }else if(counter%4===1){
                    this.messageList[this.chatGPTMessageIndex].text = ".."
                }else if(counter%4===2){
                    this.messageList[this.chatGPTMessageIndex].text = "..."
                }else {
                    this.messageList[this.chatGPTMessageIndex].text = "...."
                }
                counter++
            },200);
        },
        inputChatGPTMessage(sseText){
            let chatGPTMSG = this.messageList[this.chatGPTMessageIndex]

            if(this.waitingIntervalId!==0){
                clearInterval(this.waitingIntervalId)
                this.waitingIntervalId = 0
                chatGPTMSG.text = ""
            }

            chatGPTMSG.text+=sseText
            this.handleScrollBottom()
        }
    }
}

const app = Vue.createApp(App);
app.use(ElementPlus);
app.mount("#app");
