;
(function ($) {

    $.fn.checkForm = function(options) {

        var defaultOptions = {
            selector: 'input,textarea',
            attentionClass: 'attention'
        };

        var settings = $.extend({}, defaultOptions, options||{});

        return this.each(function() {
            var $form = $(this),
                $inputs = $form.find(settings.selector),
                $responseMessage = $form.find('.response-message').hide();

            function sendMessage(json){
                $.ajax({
                    type:'POST',
                    url:'/w/sendMail',
                    data:json
                })
                .success(function(){
                    $form.reset();
                }).error(function(response){
                    $responseMessage
                        .html(response)
                        .show();
                });
            }

            $responseMessage.click(function(){
                $(this).hide();
            });

            // on submit event
            $form.submit(function(){

                var json = {},
                    isValid = true;

                $inputs.each(function(){
                    var $element = $(this);
                    // check is empty
                    if($element.attr('name') == 'abForm') {
                        if($element.val() != '') {
                            isValid = false;
                        }
                    // check isnt empty
                    } else {
                        if($element.val() == '') {
                            $element.addClass(settings.attentionClass);
                            isValid = false;
                        } else {
                            $element.removeClass(settings.attentionClass);
                        }
                    }
                    // validate specials
                    switch($element.attr('name')){
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

                    if(isValid && !$element.hasClass(settings.attentionClass)){
                        json[$element.attr('name')] = $element.val();
                    }

                });

                if(isValid && $form.find('.'+settings.attentionClass).length == 0){
                    sendMessage(json);
                }

                return false;
            });
        });
    };
})(jQuery);