Imports System.Text
Imports Microsoft.VisualStudio.TestTools.UnitTesting
Imports Einvoice.External.Client.WebReference

<TestClass()> Public Class MainTest

    <TestMethod()> Public Sub ServiceTest()

        Dim ref As New PortalService
        ref.Url = "http://carlos-trabajo:8080/einvoice/external/queries/Portal"

        Dim docs As webDocument()
        docs = ref.findDocuments("20546236740", "20563330709", "2015-06")

    End Sub

End Class