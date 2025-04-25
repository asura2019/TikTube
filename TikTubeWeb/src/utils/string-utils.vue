<script>
function clarityDisplay(pixelsNumber) {
  if (pixelsNumber == null || pixelsNumber === '') {
    return
  }
  if (pixelsNumber >= 8294400) {
    return '4K'
  }
  if (pixelsNumber >= 2073600) {
    return 'HD'
  }
  return 'SD'
}

function dataErrorMessage(data) {
  let result = ''
  if (data != null) {
    for (const key in data) {
      result += data[key] + '\n'
    }
    return result
  }
  return '数据校验错误，请检查数据后重新提交'
}

export default {
  clarityDisplay,
  dataErrorMessage,
}
</script>
