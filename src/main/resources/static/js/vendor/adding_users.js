var current=3;
function addline()
{
    var form = document.getElementById('inserting_form');
    var div = document.createElement('div');
    div.setAttribute("align", "center");
    div.setAttribute("class", "form-inline");
    div.innerHTML = '<input type="email" class="form-control" id="exampleInputEmail1" placeholder="email"><button class="btn btn-danger btn-sm">Удалить</button>';
    form.insertBefore(div, form.children[current]);
    current++;
    return false;
}