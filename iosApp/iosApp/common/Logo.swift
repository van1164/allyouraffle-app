import SwiftUI

struct Logo: View {
    var fontSize: CGFloat = 40

    var body: some View {
        HStack {
            Text("AllYouRaffle")
                .font(.custom("Dancing Script", size: fontSize))
                .multilineTextAlignment(.center)
                .fontWeight(.heavy)
                .foregroundColor(Color("Main")) // 테마 색상에 맞춰 변경
                .shadow(color: Color.gray, radius: 3, x: 3, y: 3)
                .padding(16)
        }
        .frame(maxWidth: .infinity)
        .multilineTextAlignment(.center) // 텍스트 중앙 정렬
    }
}

struct LogoPreview: PreviewProvider {
    init(){
    }
    static var previews: some View {
        Logo().onAppear{

        }
    }
}
