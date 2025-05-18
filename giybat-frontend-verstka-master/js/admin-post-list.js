import AppConfig from "./AppConfig.js";
window.addEventListener("DOMContentLoaded", function () {
    getPostList();
});
let currentPage = 1;
let pageSize = 10;

function getPostList() {
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    let postQuery = document.getElementById("admin_post_list_post_input_id");
    let profileQuery = document.getElementById("admin_post_list_profile_input_id")
    const body = {
        "postQuery": postQuery.value,
        "profileQuery": profileQuery.value
    }
    if (postQuery.value.length === 0 || postQuery.value.trim().length === 0) {
        postQuery = null;
    }
    if (profileQuery.value.length === 0 || profileQuery.value.trim().length === 0) {
        postQuery = null;
    }
    fetch(AppConfig.API+'/api/v1/post/filter?page=' + currentPage + '&size=' + pageSize, {
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
            showPostList(data)
            showPagination(data.totalElements, data.size)
        })
        .catch(error => {
            console.error('Error:', error);
        });

}

function showPostList(postList) {
    const parent = document.getElementById("admin_post_list_table_id")
    parent.innerHTML='';
    postList.content.forEach((post, count) => {
        const tr = document.createElement("tr")
        //number
        const tdNumber = document.createElement("td")
        tdNumber.classList.add("td")
        tdNumber.innerHTML = (currentPage - 1) * pageSize + count + 1

        //tdImage
        const tdImage = document.createElement("td");
        tdImage.classList.add("td");

        //image
        const image = document.createElement("img");
        if (post.attachDTO && post.attachDTO.url) {
            image.src = post.attachDTO.url;
        } else {
            image.src = "./images/photo.png"
        }
        image.classList.add("table_photo")
        tdImage.appendChild(image)

        // TITLE
        const tdTitle = document.createElement("td")
        tdTitle.classList.add("td")
        tdTitle.textContent = post.title

        // CreatedDate
        const tdCreatedDate = document.createElement("td")
        tdCreatedDate.classList.add("td")
        tdCreatedDate.textContent = dateFormat(post.createdDate)

        // name
        const tdName = document.createElement("td")
        tdName.classList.add("td")
        tdName.innerHTML = post.profile.name+"<br>"+post.profile.username

        // tdButton DELETE
        const tdButtonDelete = document.createElement("td")
        tdButtonDelete.classList.add("td","d-flex")
        //Button INFO
        const infoButton = document.createElement("img")
        infoButton.src = "./images/info.png"
        infoButton.classList.add("table_basket", "hover-pointer")
        infoButton.addEventListener("click", () => {
            // window.location.href=  "./post-detail.html?id=" + post.id
            window.open("./post-detail.html?id=" + post.id,'_blank');
        })
        tdButtonDelete.appendChild(infoButton)
        //Button DELETE
        const deleteButton = document.createElement("img")
        deleteButton.src = "./images/basket.svg"
        deleteButton.classList.add("table_basket", "hover-pointer")
        deleteButton.addEventListener("click", () => {
            deletePost(post.id)
        })
        tdButtonDelete.appendChild(deleteButton)






        //append child
        tr.appendChild(tdNumber);
        tr.appendChild(tdImage);
        tr.appendChild(tdTitle);
        tr.appendChild(tdCreatedDate);
        tr.appendChild(tdName);
        tr.appendChild(tdButtonDelete);

        //
        parent.appendChild(tr);

    })
}

function showPagination(totalElements) {
    let totalPageCount = Math.ceil(totalElements / pageSize);

    const paginationWrapper = document.getElementById("paginationWrapperId");
    paginationWrapper.innerHTML = '';

    // previous button
    const prevDiv = document.createElement("div");
    prevDiv.classList.add("pagination_btn__box");

    const prevButton = document.createElement("button");
    prevButton.classList.add("pagination_btn", "pagination-back");
    prevButton.textContent = "Oldinga";
    prevButton.onclick = () => {
        if (currentPage > 1) {
            currentPage--;
            getPostList();
        }
    }
    prevDiv.appendChild(prevButton);
    paginationWrapper.appendChild(prevDiv);

    // page numbers
    const pageNumberWrapper = document.createElement("div");
    pageNumberWrapper.classList.add("pagination_block");

    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPageCount, currentPage + 2);

    if (startPage > 1) { // show first page
        addBtn(1, pageNumberWrapper, false, false)
        if (startPage > 2) { // add ...
            addBtn("...", pageNumberWrapper, false, true)
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        addBtn(i, pageNumberWrapper, i === currentPage)
    }

    if (endPage < totalPageCount) { // show last page
        if (endPage < totalPageCount - 1) { // add ...
            addBtn("...", pageNumberWrapper, false, true)
        }
        addBtn(totalPageCount, pageNumberWrapper, false, false)
    }


    paginationWrapper.appendChild(pageNumberWrapper);

    // next button
    const nextDiv = document.createElement("div");
    nextDiv.classList.add("pagination_btn__box");
    const nextButton = document.createElement("button");
    nextButton.classList.add("pagination_btn", "pagination-forward");
    nextButton.textContent = "Keyingi";
    nextButton.onclick = () => {
        if (currentPage < totalPageCount) {
            currentPage++;
            getPostList();
        }
    }

    nextDiv.appendChild(nextButton);
    paginationWrapper.appendChild(nextDiv);
}

function addBtn(btnText, pageNumberWrapper, isSelected, isDots) {
    const btnWrapper = document.createElement("div");
    btnWrapper.classList.add("pagination_btn__box");
    const btn = document.createElement("button");
    btn.textContent = btnText;
    if (isDots) {
        btn.classList.add("pagination_btn_dots");
    } else {
        if (isSelected) {
            btn.classList.add("pagination_active");
        } else {
            btn.classList.add("pagination_btn");

            btn.onclick = () => {
                currentPage = btnText;
                getPostList();
            }
        }
    }


    btnWrapper.appendChild(btn);
    pageNumberWrapper.appendChild(btnWrapper);
}

function deletePost(postId) {
    if (!confirm("Postni o'chirmoqchimisiz?")) {
        return;
    }
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    fetch(AppConfig.API+'/api/v1/post/' + postId, {
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
            alert(data)
            getPostList()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}





