// window.onload = function () {
//     getPostList();
// };
import AppConfig from "./AppConfig.js";

window.addEventListener("DOMContentLoaded", function () {
    getPostList();
});

let currentPage = 1;

function getPostList() {
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    let size = 6;
    fetch(AppConfig.API + '/api/v1/post/profile?page=' + currentPage + '&size=' + size, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            showPostList(data)
            if (data.totalElements > data.size) {
                showPagination(data.totalElements, data.size);
            } else {
                document.getElementById('paginationWrapperId').style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function showPostList(postList) {
    const parent = document.getElementById("profile_post_container_id")
    parent.innerHTML = '';
    postList.content.forEach(post => {
        const div = document.createElement("div");
        div.classList.add("position-relative", "post_box");

        //button
        const editButton = document.createElement("a")
        editButton.classList.add("profile_tab_btn")
        editButton.href = "./post-create.html?id=" + post.id

        //image div
        const imageDiv = document.createElement("div");
        div.classList.add("post_img__box");
        //image
        const image = document.createElement("img");
        if (post.attachDTO && post.attachDTO.url) {
            image.src = post.attachDTO.url;
        } else {
            image.src = "./images/book1.png"
        }
        image.classList.add("post_img")
        imageDiv.appendChild(image)

        // title
        const title = document.createElement("h3")
        title.classList.add("post_title")
        title.textContent = post.title


        // created date
        const createdDate = document.createElement("p")
        createdDate.classList.add("post_text")
        createdDate.textContent = dateFormat(post.createdDate)


        //add elements to main div
        div.appendChild(editButton)
        div.appendChild(imageDiv)
        div.appendChild(title)
        div.appendChild(createdDate)

        parent.appendChild(div);
    })
}

function showPagination(totalElements, size) {
    let totalPageCount = Math.ceil(totalElements / size);

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
                getPostList()
            }
        }
    }


    btnWrapper.appendChild(btn);
    pageNumberWrapper.appendChild(btnWrapper);
}






