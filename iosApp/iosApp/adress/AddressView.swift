import SwiftUI
import shared

struct AddressView : View{
    var userInfo : UserInfoResponse
    var body: some View {
        Text(userInfo.name)
        Text(userInfo.email)
        Text(userInfo.address?.description() ?? "XX")
    }
}
