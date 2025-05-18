import AppConfig from "./AppConfig.js";
let currentPage = 1;
let query = null;

window.addEventListener("DOMContentLoaded", function () {
    var url_string = window.location.href; // www.test.com?id=dasdasd
    var url = new URL(url_string);
    var urlQuery = url.searchParams.get("query");
    if (urlQuery) {
        query = urlQuery;
        getPostList(query);
    }else {
        getPostList(null)
    }


    // search input
    const searchBtn = document.getElementById("srp-search-buttonId");
    if (searchBtn) {
        searchBtn.addEventListener("click", (e) => {
            e.preventDefault();
            const query = document.getElementById("srp-search-inputId").value;
            if (query && query.length > 0) {
                window.location.href = "./search-result-page.html?query=" + query;
            }
        })
    }

    const searchInput = document.getElementById("srp-search-inputId");
    if (searchInput) {
        searchInput.addEventListener("keypress", (e) => {
            if (e.key === "Enter") {
                e.preventDefault()
                if (searchInput.value && searchInput.value.length > 0) {
                    window.location.href = "./search-result-page.html?query=" + searchInput.value;
                }
            }
        })
    }
});

function getPostList(query) {
    const lang = document.getElementById("current-lang").textContent;
    let size = 9;
    const body = {
        "query": query
    }
    fetch(AppConfig.API+'/api/v1/post/public/filter?page=' + currentPage + '&size=' + size, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
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
            document.getElementById("searchQueryTagId").textContent = "Qidiruv " + "'" + query + "'";
            document.getElementById("searchQueryResultTagId").textContent = data.totalElements + " ta element topildi"
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function showPostList(postList) {
    const parent = document.getElementById("post-search-result-containerId")
    parent.innerHTML = '';
    if (!postList.content || postList.content.length === 0) {
        const message = document.createElement("p");
        message.textContent = "Hech qanday natija topilmadi.";
        parent.appendChild(message);

        // Clear pagination
      document.getElementById("paginationWrapperId").style.visibility='hidden';

    }
    postList.content.forEach(post => {

        // POST BOX
        const div = document.createElement("div")
        div.classList.add("post_box")
        // POST DETAIL
        const a = document.createElement("a")
        //
        a.href = "./post-detail.html?id=" + post.id
        // IMAGE DIV
        const imageDiv = document.createElement("div")
        imageDiv.classList.add("post_img__box")
        //IMAGE
        const img = document.createElement("img")
        if (post.attachDTO && post.attachDTO.url) {
            img.src = post.attachDTO.url;
        } else {
            img.src = "./images/book1.png"
        }
        img.classList.add("post_img")
        imageDiv.appendChild(img)
        //TITLE
        const postTitle = document.createElement("h3")
        postTitle.classList.add("post_title")
        postTitle.textContent = post.title
        //CREATED DATE
        const date = document.createElement("p")
        date.classList.add("post_text")
        date.textContent = dateFormat(post.createdDate)
        //check the number of returned elements


        //add child
        a.appendChild(postTitle)
        a.appendChild(date)
        a.appendChild(imageDiv)
        div.appendChild(a)
        parent.appendChild(div)

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
                getPostList();
            }
        }
    }


    btnWrapper.appendChild(btn);
    pageNumberWrapper.appendChild(btnWrapper);
}
