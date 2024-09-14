import shared
import SwiftUI

@MainActor
protocol BaseObserver {
    func setError(message : String)
    
    func setErrorNull()
}
