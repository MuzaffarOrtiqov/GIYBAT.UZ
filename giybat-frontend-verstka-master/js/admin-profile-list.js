import AppConfig from "./AppConfig.js";

window.addEventListener("DOMContentLoaded", function () {
    getProfileList();
});
let currentPage = 1;
let pageSize = 10;

function getProfileList() {
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const body = {
        "title": null             //TODO add value to body
    }
    const lang = document.getElementById("current-lang").textContent;
    fetch(AppConfig.API+'/api/v1/profile/filter?page=' + currentPage + '&size=' + pageSize, {
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
            showProfileList(data)
            showPagination(data.totalElements, data.size)

        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function showProfileList(profileList) {
    const parent = document.getElementById("profile_list_container_id")
    parent.innerHTML = '';
    profileList.content.forEach((profile, count) => {
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
        if (profile.attachDTO && profile.attachDTO.url) {
            image.src = profile.attachDTO.url;
        } else {
            image.src = "./images/photo.png"
        }
        image.classList.add("table_photo")
        tdImage.appendChild(image)

        // Name
        const tdName = document.createElement("td")
        tdName.classList.add("td")
        tdName.textContent = profile.name

        // username
        const tdUsername = document.createElement("td")
        tdUsername.classList.add("td")
        tdUsername.textContent = profile.username

        // CreatedDate
        const tdCreatedDate = document.createElement("td")
        tdCreatedDate.classList.add("td")
        tdCreatedDate.textContent = dateFormat(profile.createdDate)

        // Post Count
        const tdPostCount = document.createElement("td")
        tdPostCount.classList.add("td")
        tdPostCount.textContent = profile.postCount

        // ROLES
        const tdRoles = document.createElement("td")
        tdRoles.classList.add("td")
        tdRoles.textContent = profile.role
        if (profile.role) {
            tdRoles.innerHTML = profile.role.join("<br>")
        }

        // tdButton BLOCK
        const tdButtonStatus = document.createElement("td")
        tdButtonStatus.classList.add("td")
        //Button BLOCK
        const button = document.createElement("button")
        button.classList.add("table_btn")
        if (profile.status === 'ACTIVE') {
            button.classList.add("table_btn_active")
            button.addEventListener("click", () => {
                changeStatus(profile.id, "BLOCKED")
            })
        } else if (profile.status === 'BLOCKED') {
            button.classList.add("table_btn_block")
            button.addEventListener("click", () => {
                changeStatus(profile.id, "ACTIVE")
            })
        } else {
            button.classList.add("table_btn_in_registration")
        }
        button.textContent = profile.status
        tdButtonStatus.appendChild(button)

        // tdButton DELETE
        const tdButtonDelete = document.createElement("td")
        tdButtonDelete.classList.add("td")
        //Button DELETE
        const deleteButton = document.createElement("img")
        deleteButton.src = "./images/basket.svg"
        deleteButton.classList.add("table_basket", "hover-pointer")
        deleteButton.addEventListener("click", () => {
            deleteProfile(profile.id)
        })
        tdButtonDelete.appendChild(deleteButton)

        //append child
        tr.appendChild(tdNumber);
        tr.appendChild(tdImage);
        tr.appendChild(tdName);
        tr.appendChild(tdUsername);
        tr.appendChild(tdCreatedDate);
        tr.appendChild(tdPostCount);
        tr.appendChild(tdRoles);
        tr.appendChild(tdButtonStatus);
        tr.appendChild(tdButtonDelete);
        //
        parent.appendChild(tr);

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
            getProfileList()
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
            getProfileList()
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
                getProfileList()
            }
        }
    }


    btnWrapper.appendChild(btn);
    pageNumberWrapper.appendChild(btnWrapper);
}

function changeStatus(userId, status) {
    if (!confirm("Statusni o'zgartirmoqchimisiz?")) {
        return;
    }
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const body = {
        "status": status
    }
    const lang = document.getElementById("current-lang").textContent;
    fetch(AppConfig.API+'/api/v1/profile/status/' + userId, {
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
            showPopup(data.message)
            getProfileList()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function deleteProfile(userId) {
    if (!confirm("Profilni o'chirmoqchimisiz?")) {
        return;
    }
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    fetch(AppConfig.API+'/api/v1/profile/' + userId, {
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
            showPopup(data.message)
            getProfileList()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}