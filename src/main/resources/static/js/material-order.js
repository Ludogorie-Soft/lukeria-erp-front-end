document.addEventListener("DOMContentLoaded", function () {
    // Select all radio buttons for material types
    const materialTypeRadios = document.querySelectorAll('input[type=radio][name=materialType]');

    // Dropdown elements
    const plateDropdown = document.getElementById('plateDropdown');
    const cartonDropdown = document.getElementById('cartonDropdown');
    const packageDropdown = document.getElementById('packageDropdown');

    // Function to show/hide dropdowns based on selected material type
    function handleMaterialTypeChange(event) {
        const selectedMaterial = event.target.value;

        // Hide all dropdowns
        plateDropdown.style.display = 'none';
        cartonDropdown.style.display = 'none';
        packageDropdown.style.display = 'none';

        // Deactivate all selects
        document.getElementById('plateId').disabled = true;
        document.getElementById('cartonId').disabled = true;
        document.getElementById('packageId').disabled = true;

        // Show the relevant dropdown and activate the corresponding select
        if (selectedMaterial === 'PLATE') {
            plateDropdown.style.display = 'block';
            document.getElementById('plateId').disabled = false;
        } else if (selectedMaterial === 'CARTON') {
            cartonDropdown.style.display = 'block';
            document.getElementById('cartonId').disabled = false;
        } else if (selectedMaterial === 'PACKAGE') {
            packageDropdown.style.display = 'block';
            document.getElementById('packageId').disabled = false;
        }
    }

    // Attach event listener to each radio button
    materialTypeRadios.forEach(radio => {
        radio.addEventListener('change', handleMaterialTypeChange);
    });

    // Initialize Select2 for all select elements
    $('#plateId').select2();
    $('#cartonId').select2();
    $('#packageId').select2({
        templateResult: formatState
    });

    // Function to display image with select option in Select2
    function formatState(state) {
        if (!state.id) {
            return state.text;
        }
        var imageUrl = state.element.getAttribute('data-image-url');
        var $state = $(
            '<span><img src="' + imageUrl + '" width="50" height="50" /> ' + state.text + '</span>'
        );
        return $state;
    }
});