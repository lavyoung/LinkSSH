import { create } from "zustand";
import { DEFAULT_HOST_ID, findHostById } from "./host.fixtures";

interface HostSelectionState {
    selectedHostId: string;
    selectHost: (hostId: string) => void;
}

export const useHostSelectionStore = create<HostSelectionState>()((set) => ({
    selectedHostId: DEFAULT_HOST_ID,

    selectHost: (hostId) => {
        if (!findHostById(hostId)) {
            return;
        }

        set({ selectedHostId: hostId });
    },
}));
