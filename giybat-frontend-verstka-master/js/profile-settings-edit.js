window.addEventListener("DOMContentLoaded", function () {
    const userDetailJon = localStorage.getItem("userDetail");
    if (!userDetailJon) {
        return;
    }

    const userDetailObj = JSON.parse(userDetailJon);

    document.getElementById("profile_settings_name").value = userDetailObj.name;
    document.getElementById("profile_settings_username").value = userDetailObj.username;
    if (userDetailObj.photo) {
        document.getElementById("profile_settings_photo").src = userDetailObj.photo.url;
    }
});

function profileDetailUpdate() {
    const name = document.getElementById("profile_settings_name").value
    if (!name) {
        alert("Please fill in the input")
        return;
    }

    const lang = document.getElementById("current-lang").textContent;
    const body = {
        "name": name
    }
    const jwt = localStorage.getItem("jwtToken")
    if (!jwt) {
        window.location.href = "./login.html"
        return;
    }

    fetch('http://localhost:8080/api/v1/profile/detail', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            return Promise.reject(response.text())
        }
    }).then(item => {
        alert(item.message)

        const userDetailJon = localStorage.getItem("userDetail");
        const userDetail = JSON.parse(userDetailJon);
        userDetail.name = name
        //header name change
        const headerUserNameSpan = document.getElementById("header_user_name_id");
        headerUserNameSpan.textContent = name;


        localStorage.setItem("userDetail", JSON.stringify(userDetail))
    }).catch(error => {
        error.then(errMessage => {
            alert(errMessage)
        })
    })
}

function profilePasswordUpdate() {
    const currentPswd = document.getElementById("profile_settings_current_pswd").value
    const newPswd = document.getElementById("profile_settings_new_pswd").value
    if (!currentPswd || !newPswd) {
        alert("Enter all inputs")
        return;
    }

    const body = {
        "currentPassword": currentPswd,
        "newPassword": newPswd
    };

    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }

    const lang = document.getElementById("current-lang").textContent;

    fetch('http://localhost:8080/api/v1/profile/password', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            return Promise.reject(response.text())
        }
    }).then(item => {
        alert(item.message)

    }).catch(error => {
        error.then(errMessage => {
            alert(errMessage)

        })
    })
}

function profileUserNameChange() {
    const username = document.getElementById("profile_settings_username").value
    if (!username) {
        alert("Enter all inputs")
        return;
    }
    const body = {
        "username": username
    }

    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }

    const lang = document.getElementById("current-lang").textContent;

    fetch('http://localhost:8080/api/v1/profile/username', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            return Promise.reject(response.text())
        }
    }).then(item => {
        //TODO ENABLE USER TO SEE TO WHOM THE CODE IS BEING SENT

        document.getElementById("confirmModalResultId").textContent=item.message
        openModal()

    }).catch(error => {
        error.then(errMessage => {
            alert(errMessage)
        })
    })

}

function profileUserNameChangeConfirm() {
    const confirmCode = document.getElementById("profileUserNameChaneConfirmInputId").value
    if (!confirmCode) {
        alert("Enter all inputs")
        return;
    }
    closeModal()
    const body = {
        "code": confirmCode
    }
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }

    const lang = document.getElementById("current-lang").textContent;

    fetch('http://localhost:8080/api/v1/profile/username-confirmation', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            return Promise.reject(response.text())
        }
    }).then(item => {
        alert(item.message)
        //jwt update in local storage
        localStorage.setItem("jwtToken", item.data)
        // username update  in  local storage
        const userDetailJon = localStorage.getItem("userDetail");
        const userDetail = JSON.parse(userDetailJon);
        userDetail.username = document.getElementById("profile_settings_username").value
        userDetail.jwt = item.data
        localStorage.setItem("userDetail", JSON.stringify(userDetail))

    }).catch(error => {
        closeModal()
        error.then(errMessage => {
            alert(errMessage)
        })
    })

}

//------------ Change username confirm modal start ------------
const modal = document.getElementById('simpleModalId');

function openModal() {
    modal.style.display = 'block';
}

function closeModal() {
    modal.style.display = "none";
}

window.onclick = (event) => {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
};

//------------ Change username confirm modal end ------------

// ------------ Image preview ------------
function previewImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function () {
        const img = document.getElementById('profile_settings_photo');
        img.src = reader.result;
    };

    if (file) {
        reader.readAsDataURL(file);
        document.getElementById('profile_settings_upload_img_btn_id').style.display = 'inline-block';
    }
}

// ------------ Image upload ------------
function uploadImage() {
    const fileInput = document.getElementById('imageUpload');
    const file = fileInput.files[0];
    if (file) {
        const formData = new FormData();
        formData.append('file', file);

        const jwt = localStorage.getItem('jwtToken');
        if (!jwt) {
            window.location.href = './login.html';
            return;
        }
        const lang = document.getElementById("current-lang").textContent;
        //
        // fetch('http://localhost:8080/attach/upload', {
        //     method: 'POST',
        //     headers: {
        //         'Accept-Language': lang,
        //         'Authorization': 'Bearer ' + jwt
        //     },
        //     body: formData
        // })
        //     .then(response => {
        //         if (!response.ok) {
        //             throw new Error('Network response was not ok');
        //         }
        //         return response.json();
        //     })
        //     .then(data => {
        //         console.log('Success:', data);
        //         if (data.id) {
        //             updateProfileImage(data.id); // profile update image
        //
        //             const userDetailJon = localStorage.getItem("userDetail");
        //             const userDetail = JSON.parse(userDetailJon);
        //             userDetail.photo = {};
        //             userDetail.photo.id = data.id;
        //             userDetail.photo.url = data.url;
        //             localStorage.setItem("userDetail", JSON.stringify(userDetail));
        //
        //             // document.getElementById("header_user_image_id").src =data.url;
        //         }
        //     })
        //     .catch(error => {
        //         console.error('Error:', error);
        //     });
    }
}

function updateProfileImage(photoId) {
    if (!photoId) {
        return;
    }

    const body = {
        "photoId": photoId
    }

    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }

    const lang = document.getElementById("current-lang").textContent;
}
