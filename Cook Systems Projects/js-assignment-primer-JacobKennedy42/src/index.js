export function calculateTotal (items, tax) {
  tax = Math.abs(tax);
  var sum = 0;
  items.forEach(item => sum += item.price + (item.taxable ? item.price*tax : 0));
  return sum;
}
