;
(function ($) {
    $.fn.checkForm = function(options) {
        var defaultOptions = {
            preSubject: '',
            selector: 'input,textarea',
            attentionClass: 'attention',
            additionalFields: [],
            optionalFields: []
        };

        var settings = $.extend({}, defaultOptions, options||{});

        return this.each(function() {
            var $form = $(this),
                $inputs = $form.find(settings.selector),
                $sendFail = $form.find('.send-fail');
                $sendSuccess = $form.find('.send-success');

            function sendMessage(json){
                $.ajax({
                    type: 'POST',
                    url: '/w/sendMail',
                    data: JSON.stringify(json)
                })
                .done(function(){
                    $form[0].reset();
                    $sendSuccess
                        .show();
                })
                .fail(function(res){
                    $sendFail
                        .html(res.responseText)
                        .show();
                });
            }

            // message clicker
            $sendFail.click(function(){
                $(this).hide();
            });
            $sendSuccess.click(function(){
                $(this).hide();
            });

            // on submit event
            $form.submit(function(){
                var json = {},
                    isValid = true;

                function addToJson(inpName, value, isMultiple){
                    if(settings.additionalFields.length > 0 && !('additionalFields' in json)){
                        json.additionalFields = {};
                    }
                    // like an array
                    if(isMultiple) {
                        // set normal
                        if (settings.additionalFields.indexOf(inpName) == -1){
                            // create array
                            if (!(inpName in json)) {
                                json[inpName] = "";
                            }
                            // push to array
                            json[inpName]+= (json[inpName] != ''?',':'') + value;
                        // set to additionalFields
                        } else {
                            if (!(inpName in json.additionalFields)) {
                                json.additionalFields[inpName] = "";
                            }
                            // push to array
                            json.additionalFields[inpName]+= (json.additionalFields[inpName] != ''?',':'') + value;
                        }
                    // strings
                    } else {
                        // set normal
                        if (settings.additionalFields.indexOf(inpName) == -1)
                            json[inpName] = value;
                        // set to additionalFields
                        else
                            json.additionalFields[inpName] = value;
                    }
                }

                $inputs.each(function(){
                    var $element = $(this)
                        inpValue = $element.val(),
                        inpName = $element.attr('name');
                    // check is empty
                    if(inpName == 'abForm') {
                        if($element.val() != '') {
                            isValid = false;
                        }
                    // check isnt empty
                    } else {
                        if(inpValue == '' && settings.optionalFields.indexOf(inpName) == -1) {
                            $element.addClass(settings.attentionClass);
                            isValid = false;
                        } else {
                            $element.removeClass(settings.attentionClass);
                        }
                    }
                    // validate specials by name
                    switch(inpName){
                        case "email":
                            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                            if(!re.test($element.val())){
                                $element.addClass(settings.attentionClass);
                                isValid = false;
                            } else {
                                $element.removeClass(settings.attentionClass);
                            }
                        break;
                    }
                    // compare json
                    if(isValid && !$element.hasClass(settings.attentionClass)){
                        var setValue = true;

                        // change value by name
                        switch(inpName) {
                            case "subject":
                                if(settings.preSubject != ''){
                                    inpValue = settings.preSubject+' '+inpValue;
                                }
                            break;
                        }
                        // change value by type
                        switch($element.attr('type')) {
                            case "checkbox":
                                setValue = false;
                                if($element[0].checked)
                                    addToJson(inpName, inpValue, true);
                            break;
                        }
                        // set value of input
                        if(setValue)
                            addToJson(inpName, inpValue);
                    }

                });

                if(isValid && $form.find('.'+settings.attentionClass).length == 0){
                    sendMessage(json);
                }

                return false;
            });
        });
    };

    $.fn.checkboxLabelChecker = function(options){
        var defaultOptions = {
            selector: 'label',
            checkedClass: 'checked'
        };

        var settings = $.extend({}, defaultOptions, options||{});

        return this.each(function(){
            var $form = $(this),
                $labels = $form.find(settings.selector);

            $labels.each(function(){
                var $element = $(this),
                    $checkbox = $element.find("input[type='checkbox']");

                if($checkbox.length > 0){
                    $checkbox.change(function(){
                        if($checkbox[0].checked){
                            $element.addClass(settings.checkedClass)
                        } else {
                            $element.removeClass(settings.checkedClass)
                        }
                    });
                }
            });
        });
    };

})(jQuery);