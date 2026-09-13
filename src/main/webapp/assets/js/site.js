// YogaMart — small progressive-enhancement touches. No framework:
// plain DOM APIs only, per the project's vanilla-JS-for-AJAX stack (spec Section 3).
document.addEventListener('DOMContentLoaded', function () {
  // Category chip active-state: highlight whichever chip matches the
  // current ?category= in the URL, so the filter feels connected to
  // what's actually showing rather than just being a static row of links.
  var params = new URLSearchParams(window.location.search);
  var activeCategory = params.get('category');
  document.querySelectorAll('.category-chip').forEach(function (chip) {
    var chipCategory = chip.getAttribute('data-category');
    if ((chipCategory === null && activeCategory === null) ||
        (chipCategory !== null && chipCategory === activeCategory)) {
      chip.classList.add('active');
    }
  });
});
