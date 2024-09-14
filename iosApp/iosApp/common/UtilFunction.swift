import Foundation
import SwiftUI
import shared
import Security

extension Kotlinx_coroutines_coreStateFlow{
    func subscribe(block:@escaping (AnyObject?) -> Void){
        CommonFlow<AnyObject>(origin: self).subscribe(block: block)
    }
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        let scanner = Scanner(string: hex)
        
        if hex.hasPrefix("#") {
            scanner.currentIndex = hex.startIndex
        }
        
        var rgb: UInt64 = 0
        scanner.scanHexInt64(&rgb)
        
        let red = Double((rgb & 0xFF0000) >> 16) / 255.0
        let green = Double((rgb & 0x00FF00) >> 8) / 255.0
        let blue = Double(rgb & 0x0000FF) / 255.0
        
        self.init(red: red, green: green, blue: blue)
    }
}

struct ToastView: View {
    var message: String

    var body: some View {
        Text(message)
            .padding()
            .background(Color.black.opacity(0.8))
            .foregroundColor(.white)
            .cornerRadius(8)
            .shadow(radius: 10)
            .transition(.opacity)
            .zIndex(1)
    }
}

extension View {
    func toast(isPresented: Bool, message: Binding<String?>, onFinished: @escaping () -> (Void)) -> some View {
        ZStack {
            self
            if isPresented, let toastMessage = message.wrappedValue {
                ToastView(message: toastMessage)
                    .padding(.bottom, 50)
                    .transition(.move(edge: .bottom))
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            onFinished()
                        }
                    }
                }
            }
        }
    }



class KeychainHelper {
    static let shared = KeychainHelper()

    func save(key: String, data: Data) {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecValueData as String: data
        ]

        SecItemDelete(query as CFDictionary) // 기존 항목 삭제
        SecItemAdd(query as CFDictionary, nil) // 새 항목 추가
    }

    func load(key: String) -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecReturnData as String: kCFBooleanTrue!,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]

        var dataTypeRef: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &dataTypeRef)

        if status == errSecSuccess {
            if let data = dataTypeRef as? Data {
                return String(data: data, encoding: .utf8)
            }
        }
        return nil
    }

    func delete(key: String) {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key
        ]
        SecItemDelete(query as CFDictionary)
    }
}

func saveJwt(jwt: String) {
    KeychainHelper.shared.save(key: "jwt", data: jwt.data(using: .utf8)!)
}

func saveRefreshToken(refreshToken: String) {
    KeychainHelper.shared.save(key: "refreshToken", data: refreshToken.data(using: .utf8)!)
}

func clearToken(){
    KeychainHelper.shared.delete(key: "jwt")
    KeychainHelper.shared.delete(key: "refreshToken")
}


func loadJwt() -> String? {
    let jwt = KeychainHelper.shared.load(key: "jwt")
    return jwt
}
func loadRefreshToken() -> String? {
    let refreshToken = KeychainHelper.shared.load(key: "refreshToken")
    return refreshToken
}


