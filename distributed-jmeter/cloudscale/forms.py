from django import forms

class UploadScenarioForm(forms.Form):
    scenario = forms.FileField()
    instance_type = forms.ChoiceField(choices=(('t2.medium', 't2.medium'),))
    num_threads = forms.IntegerField()
    host = forms.CharField(max_length=255)
