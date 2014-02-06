from django import forms

class UploadScenarioForm(forms.Form):
    scenario = forms.FileField()
    virtual_users = forms.CharField(max_length=6)