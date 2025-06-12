document.addEventListener("DOMContentLoaded", function () {
    const menuButtons = document.querySelectorAll(".menu-button");

    menuButtons.forEach(button => {
        button.addEventListener("click", function (event) {
            console.log("メニューボタンがクリックされました"); // デバッグ用
			event.preventDefault(); // デフォルトの動作を無効化（ページのリロードを防ぐ）
            const submenu = this.nextElementSibling;
            if (submenu) {
                submenu.classList.toggle("active");
                console.log("サブメニューの表示切り替え"); // デバッグ用
            }
        });
    });
});
