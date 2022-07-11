function test(aa) {
    let result = aa + 1
    setTimeout( ()=> console.log(result), 1000);
}

test(1);
test(2);