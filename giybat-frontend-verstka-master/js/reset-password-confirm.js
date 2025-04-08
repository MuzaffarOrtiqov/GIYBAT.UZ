function resetPasswordConfirm() {
    const confirmCodeValue = document.getElementById("confirm_code").value;
    const newPasswordValue = document.getElementById("new_password").value;
    const username = localStorage.getItem("username");

    if (!confirmCodeValue || !newPasswordValue || !username) {
        alert("Please fill all inputs");
        return;
    }

    const body = {
        "username": username,
        "confirmCode": confirmCodeValue,
        "password": newPasswordValue,
    }

    const lang = document.getElementById("current-lang").textContent;

    fetch('http://localhost:8080/api/v1/auth/password-reset-confirm', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            return Promise.reject(response.text())
        }
    }).then(item => {
        alert(item.data)
        window.location.href = "./login.html";
    }).catch(error => {
        error.then(errMessage => {
            alert(errMessage)
        })
    })
}