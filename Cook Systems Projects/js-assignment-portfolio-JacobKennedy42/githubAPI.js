function loadImage (imageURL) {
    const imageElement = document.createElement("img");
    imageElement.src = imageURL;
    const container = document.getElementById("image-container");
    container.appendChild(imageElement);
}
function loadBio (bio) {
    console.log(bio);
    const container = document.getElementById("bio-container");
    container.textContent += bio;
}

function fetchObject () {
    fetch('https://api.github.com/users/JacobKennedy42')
    .then((response) => response.json())
    .then((json) => {
        loadImage(json.avatar_url);
        loadBio(json.bio);
    });
}

window.onload = async () => {
    return await fetchObject()
}