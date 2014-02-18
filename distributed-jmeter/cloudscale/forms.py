from django import forms

class UploadScenarioForm(forms.Form):
    scenario = forms.FileField()
