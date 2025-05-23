import AppConfig from "./AppConfig.js";
let postId = null;
let currentPost = null;

async function createPost() {

    const fileInput = document.getElementById('post_image_id');
    const file = fileInput.files[0];
    const titleValue = document.getElementById("post_title_id").value;
    const contentValue = document.getElementById("post_content_id").value;

    if (!file || !titleValue || !contentValue) {
        alert("Enter all inputs")
        return;
    }

    // image upload
    const imageId = await uploadImage();

    const body = {
        "title": titleValue,
        "content": contentValue,
        "photo": {
            "photoId": imageId
        }
    }

    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    fetch(AppConfig.API+'/api/v1/post', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            // Show a success alert
            alert("✅ Post created successfully!");
            window.location.href = "./profile-post-list.html"
        })
        .catch(error => {
            console.error('Error:', error);
        });


}

// ------------ Image preview ------------
function previewImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function () {
        const imgContainer = document.getElementById('post_image_block');
        imgContainer.style.backgroundImage = 'url(' + reader.result + ')';
    };
    if (file) {
        reader.readAsDataURL(file);
    }
}

// ------------ Image upload ------------
async function uploadImage() {
    const fileInput = document.getElementById('post_image_id');
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

        return fetch('http://localhost:8080/api/v1/attach/upload', {
            method: 'POST',
            headers: {
                'Accept-Language': lang,
                'Authorization': 'Bearer ' + jwt
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.id) {
                    return data.id;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                return null;
            });
    }
}

window.addEventListener("DOMContentLoaded", function () {
    var url_string = window.location.href; // www.test.com?id=dasdasd
    var url = new URL(url_string);
    var id = url.searchParams.get("id");
    if (id) {
        getPostById(id);
        document.getElementById("post_create_btn_group").classList.add("display-none");
        document.getElementById("post_update_btn_group").classList.remove("display-none");
        document.getElementById("post_page_title_id").textContent = 'G\'iybatni o\'zgartirsh';
    }
});

function getPostById(postId) {
    const lang = document.getElementById("current-lang").textContent;

    fetch(AppConfig.API+'/api/v1/post/public/' + postId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
        },

    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            currentPost = data
            //show image
            if (data.attachDTO && data.attachDTO.url) {
                const imgContainer = document.getElementById('post_image_block');
                imgContainer.style.backgroundImage = 'url(' + data.attachDTO.url + ')';
            }
            //show title //
            document.getElementById("post_title_id").value = data.title
            //show content
            document.getElementById("post_content_id").textContent = data.content

        })
        .catch(error => {
            console.error('Error:', error);
        });
}

async function updatePost() {
    if (currentPost == null) {
        return;
    }

    const fileInput = document.getElementById('post_image_id');
    const file = fileInput.files[0];

    let imageId = null;
    if (file) {
        imageId = await uploadImage();
    } else {
        imageId = currentPost.attachDTO.id;
    }

    const titleValue = document.getElementById("post_title_id").value;
    const contentValue = document.getElementById("post_content_id").value;
    const lang = document.getElementById("current-lang").textContent;
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }

    if (!imageId || !titleValue || !contentValue) {
        alert("Enter all inputs")
        return;
    }

    const body = {
        "title": titleValue,
        "content": contentValue,
        "photo": {
            "photoId": imageId
        }
    }
    fetch(AppConfig.API+'/api/v1/post/' + currentPost.id, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            alert("Success")
            window.location.href = "./profile-post-list.html"
        })
        .catch(error => {
            console.error('Error:', error);
        });


}

function deletePost() {
    if (currentPost == null) {
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    if (!confirm("G'iybatni o'chirmoqchimisiz?")) {
        return;
    }
    fetch(AppConfig.API+'/api/v1/post/' + currentPost.id, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            alert("Success")
            window.location.href = "./profile-post-list.html"
        })
        .catch(error => {
            console.error('Error:', error);
        });



}

// expose the function to the global scope for the inline HTML handler
window.previewImage = previewImage;
window.createPost = createPost;
window.updatePost = updatePost;
window.deletePost = deletePost;


