const apiUrl = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '')

function ConferenceApi() {
    function create(name, hostName, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {    
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/1/conference/create?conference_name=${encodeURIComponent(name)}&host_name=${encodeURIComponent(hostName)}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send("")
    }

    function joinSpeaker(conferenceId, speakerName, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {    
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/1/conference/join_speaker?conference_id=${conferenceId}&speaker_name=${encodeURIComponent(speakerName)}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send("")
    }

    function chunk(conferenceId, chunk, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/1/conference/chunk?conference_id=${conferenceId}&include_participants=true`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send(chunk)
    }

    function finish(conferenceId, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/1/conference/finish?conference_id=${conferenceId}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send("")
    }

    function download(conferenceId, callback) {
            callback = callback || function() {};

            const request = new XMLHttpRequest()
            request.onreadystatechange = function() {
                if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                    callback(JSON.parse(this.responseText))
                }
            }
            request.open("GET", `${apiUrl}/1/conference/download?conference_id=${conferenceId}&format=json`, true)
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
            request.send("")
    }

    return {
        "create": create,
        "joinSpeaker": joinSpeaker,
        "chunk": chunk,
        "finish": finish,
        "download": download
    }
}

function TranscribeApi() {
    function checkState(transcribeId, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("GET", `${apiUrl}/2/transcribe/check_state?transcribe_id=${transcribeId}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send("")
    }

    function get(transcribeId, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("GET", `${apiUrl}/2/transcribe/get?transcribe_id=${transcribeId}&format=editor`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send("")
    }

    function edit(transcribeId, editedText, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/2/transcribe/edit?transcribe_id=${transcribeId}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send(editedText)
    }

    return {
        "checkState": checkState,
        "get": get,
        "edit": edit
    }
}

function SpeechpadApi() {
    function create(model, name, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/1/speechpad/create?model=${model}&name=${name}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send("")
    }

    function chunk(speechpadId, chunk, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                callback(JSON.parse(this.responseText))
            }
        }
        request.open("POST", `${apiUrl}/1/speechpad/chunk?speechpad_id=${speechpadId}`, true)
        request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('access-token'))
        request.send(chunk)
    }

    return {
        "create": create,
        "chunk": chunk
    }
}

function UserApi() {
    function login(login, password, callback) {
        callback = callback || function() {};

        const request = new XMLHttpRequest()
        request.onreadystatechange = function() {
            if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                const accessToken = JSON.parse(this.responseText).accessToken
                localStorage.setItem('access-token', accessToken);
                callback()
            }
        }
        request.open("POST", `${apiUrl}/1/user/login`, true)
        request.send(JSON.stringify({"login": login, "password": password}))
    }

    return {
        "login": login
    }
}